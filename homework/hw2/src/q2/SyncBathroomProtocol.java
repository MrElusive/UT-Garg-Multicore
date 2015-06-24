import java.lang.Thread;
import java.util.Random;
import java.lang.RuntimeException;
import java.lang.InterruptedException;

public class SyncBathroomProtocol implements BathroomProtocol {
   private static boolean DEBUG = false;
   private static boolean DEBUG_VERBOSE = false;

	private volatile int numMalesInLine;
	private volatile int numFemalesInLine;

	private volatile int numMalesInBathroom;
	private volatile int numFemalesInBathroom;

   private enum Gender {
      MALE,
      FEMALE
   };

   private volatile Gender turn;
	
	public SyncBathroomProtocol() {
		numMalesInLine = 0;
		numFemalesInLine = 0;

		numMalesInBathroom = 0;
		numFemalesInBathroom = 0;

      turn = Gender.MALE;
	}

   private boolean isGenderContendingForBathroom(Gender gender) {
      return (gender == Gender.MALE) ? numMalesInLine > 0 : numFemalesInLine > 0;
   }

   private boolean isGenderInBathroom(Gender gender) {
      return (gender == Gender.MALE) ? numMalesInBathroom > 0 : numFemalesInBathroom > 0;
   }

   private boolean isGendersTurn(Gender gender) {
      switch (gender) {
         case MALE:
            return turn == Gender.MALE;
         case FEMALE:
            return turn == Gender.FEMALE;
         default:
            throw new RuntimeException("Unknown gender!");
      }
   }
   
   private void incrementGenderInBathroom(Gender gender) {
      switch (gender) {
         case MALE:
            numMalesInBathroom++;
            break;
         case FEMALE:
            numFemalesInBathroom++;
            break;
         default:
            throw new RuntimeException("Unknown gender!");
      }
   }

   private void decrementGenderInBathroom(Gender gender) {
      switch (gender) {
         case MALE:
            numMalesInBathroom--;
            break;
         case FEMALE:
            numFemalesInBathroom--;
            break;
         default:
            throw new RuntimeException("Unknown gender!");
      }
   }

   private void incrementGenderInLine(Gender gender) {
      switch (gender) {
         case MALE:
            numMalesInLine++;
            break;
         case FEMALE:
            numFemalesInLine++;
            break;
         default:
            throw new RuntimeException("Unknown gender!");
      }
   }

   private void decrementGenderInLine(Gender gender) {
      switch (gender) {
         case MALE:
            numMalesInLine--;
            break;
         case FEMALE:
            numFemalesInLine--;
            break;
         default:
            throw new RuntimeException("Unknown gender!");
      }
   }

   private boolean shouldGenderEnter(Gender gender, Gender oppositeGender, boolean isWaiting) {
      if (!isGenderInBathroom(oppositeGender) && !isGenderContendingForBathroom(oppositeGender)) {
         return true;
      }

      if (!isGenderInBathroom(oppositeGender) && isGendersTurn(gender) && isWaiting) {
         return true;
      }

      return false;
   }

   private void debugGenderBegin(Gender gender) {
      if (!DEBUG) return;

      switch (gender) {
         case MALE:
            if (numMalesInBathroom == 1) {
               System.out.println("MALE REIGN - BEGINS"); 
            }
            break;
         case FEMALE:
            if (numFemalesInBathroom == 1) {
               System.out.println("FEMALE REIGN - BEGINS"); 
            }
            break;
         default:
            throw new RuntimeException("Unknown gender!");
      }
   }

   private void debugGenderEnd(Gender gender) {
      if (!DEBUG) return;

      switch (gender) {
         case MALE:
            if (numMalesInBathroom == 0) {
               System.out.println("MALE REIGN - END"); 
            }
            break;
         case FEMALE:
            if (numFemalesInBathroom == 0) {
               System.out.println("FEMALE REIGN - END"); 
            }
            break;
         default:
            throw new RuntimeException("Unknown gender!");
      }
   }

   private void debugGenderLeave(Gender gender) {
      if (!DEBUG) return;

      long currentThreadId = Thread.currentThread().getId();
      System.out.println(String.format("[%d] %s left the bathroom", currentThreadId, gender));
   }

   private void debugConditions(Gender gender, Gender oppositeGender, boolean isWaiting) {
      if (!DEBUG_VERBOSE) return;

      long currentThreadId = Thread.currentThread().getId();
      System.out.println(String.format("[%d] CONDITIONS - BEGIN", currentThreadId));
      System.out.println(String.format("-I am a %s", gender));
      System.out.println(String.format("-Is %s in bathroom? %s", gender, isGenderInBathroom(gender)));
      System.out.println(String.format("-Is %s in bathroom? %s", oppositeGender, isGenderInBathroom(oppositeGender)));
      System.out.println(String.format("-Is %s contending for bathroom? %s", gender, isGenderContendingForBathroom(gender)));
      System.out.println(String.format("-Is %s contending for bathroom? %s", oppositeGender, isGenderContendingForBathroom(oppositeGender)));
      System.out.println(String.format("-Is %s genders turn? %s", gender, isGendersTurn(gender)));
      System.out.println(String.format("-Is %s genders turn? %s", oppositeGender, isGendersTurn(oppositeGender)));
      System.out.println(String.format("-Is waiting? %s", isWaiting));
      System.out.println(String.format("-Am I going to wait? %s", !shouldGenderEnter(gender, oppositeGender, isWaiting)));
      System.out.println(String.format("[%d] CONDITIONS - END", currentThreadId));
   }

   public synchronized void enterGender(Gender gender, Gender oppositeGender) {
      boolean isWaiting = false;
      incrementGenderInLine(gender);

      while (true) {
         debugConditions(gender, oppositeGender, isWaiting);

         if (shouldGenderEnter(gender, oppositeGender, isWaiting)) {
            break;
         }

         isWaiting = true;

         try {
            wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      
      decrementGenderInLine(gender);
      incrementGenderInBathroom(gender);

      debugGenderBegin(gender);
   }

   public synchronized void leaveGender(Gender gender, Gender oppositeGender) {
      debugGenderLeave(gender);
		
      decrementGenderInBathroom(gender);

      if (!isGenderInBathroom(gender)) {
         notifyAll();
         turn = oppositeGender;
         debugGenderEnd(gender);
      }
   }

	public void enterMale() {
      final Gender gender = Gender.MALE;
      final Gender oppositeGender = Gender.FEMALE;
      enterGender(gender, oppositeGender);
	}

	public void leaveMale() {
      final Gender gender = Gender.MALE;
      final Gender oppositeGender = Gender.FEMALE;
      leaveGender(gender, oppositeGender); 
	}

	public void enterFemale() {
      final Gender gender = Gender.FEMALE;
      final Gender oppositeGender = Gender.MALE;
      enterGender(gender, oppositeGender);
	}

	public void leaveFemale() {
      final Gender gender = Gender.FEMALE;
      final Gender oppositeGender = Gender.MALE;
      leaveGender(gender, oppositeGender); 
	}
	
	public static void main(String[] args) {
      if (args.length > 0) {
         if (args[0].equals("DEBUG")) {
            SyncBathroomProtocol.DEBUG = true;
         } else if (args[0].equals("DEBUG_VERBOSE")) {
            SyncBathroomProtocol.DEBUG = true;
            SyncBathroomProtocol.DEBUG_VERBOSE = true;
         }
      }

		int numMaleThreads = 10;
		int numFemaleThreads = 10;
		final SyncBathroomProtocol syncBathroomProtocol = new SyncBathroomProtocol();
		Thread[] maleThreads = new Thread[numMaleThreads];
		Thread[] femaleThreads = new Thread[numFemaleThreads];
		
		Runnable maleBusiness = new Runnable() {
			public void run() {
            Random random = new Random();
				while (true) {
               try {
                  Thread.sleep(random.nextInt(10) + 1);
                  syncBathroomProtocol.enterMale();
                  System.out.println("[" + Thread.currentThread().getId() + "] I'm male, and I'm doing my business.");
                  Thread.sleep(random.nextInt(10) + 1);
                  syncBathroomProtocol.leaveMale();
               } catch(InterruptedException e) {

               }
				}
			}
		};
		
		Runnable femaleBusiness = new Runnable() {
			public void run() {
            Random random = new Random();
				while (true) {
               try {
                  Thread.sleep(random.nextInt(10) + 1);
                  syncBathroomProtocol.enterFemale();
                  System.out.println("[" + Thread.currentThread().getId() + "] I'm female, and I'm doing my business.");
                  Thread.sleep(random.nextInt(10) + 1);
                  syncBathroomProtocol.leaveFemale();
               } catch(InterruptedException e) {

               }
				}
			}
		};
		
		for (int i = 0; i < numMaleThreads; i++) {
			maleThreads[i] = new Thread(maleBusiness);
		}
		
		for (int i = 0; i < numFemaleThreads; i++) {
			femaleThreads[i] = new Thread(femaleBusiness);
		}
		
		
		for (int i = 0; i < numFemaleThreads; i++) {
			femaleThreads[i].start();
		}
		for (int i = 0; i < numMaleThreads; i++) {
			maleThreads[i].start();
		}
	}
}
