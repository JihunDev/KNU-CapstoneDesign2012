// In file eventgui/ex1/Telephone.java
 import java.util.Vector;

 public class Handle {

     private Vector handleListeners = new Vector();

     public void sendMsg(byte[] msg, int state) {

         fireHandle(msg, state);
     }

     public synchronized void addHandleListener(
         HandleListener l) {

         if (handleListeners.contains(l)) {
             return;
         }

         handleListeners.addElement((Object)l);
     }

     public synchronized void removeHandleListener(
         HandleListener l) {

         handleListeners.removeElement(l);
     }

     private void fireHandle(byte[] msg, int state) {

         Vector tl;
         tl = (Vector) handleListeners.clone();

         int size = tl.size();

         if (size == 0) {
             return;
         }

         HandleEvent event = new HandleEvent(this, msg, state);

         for (int i = 0; i < size; ++i) {

             HandleListener listener =
                 (HandleListener) tl.elementAt(i);
             listener.handleEvent(event);
         }
     }


}