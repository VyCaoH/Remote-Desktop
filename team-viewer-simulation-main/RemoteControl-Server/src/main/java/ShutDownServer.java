public class ShutDownServer {
    public static void computer(){
        Runtime runtime = Runtime.getRuntime();
      try
      {
         System.out.println("Shutting down the PC after 10 seconds.");
         runtime.exec("shutdown -s -t 10");
      }
     catch (Exception e) {
            DebugMessage.printDebugMessage(e);
        }
    }
}