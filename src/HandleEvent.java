// In file eventgui/ex1/TelephoneEvent.java
public class HandleEvent
    extends java.util.EventObject {
      byte[] msg;
      int state;
      
      public HandleEvent(Handle source, byte[] msg, int state) {
        super(source);
        this.msg=msg;
        this.state=state;
    }
}