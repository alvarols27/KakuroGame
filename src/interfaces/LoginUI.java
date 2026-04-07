package interfaces;

import java.awt.event.ActionListener;

public interface LoginUI {
    String getUsername();
    String getPassword();
    void showError(String message);
    void showSuccess(String message);
    void close();
    void setVisible(boolean visible);
    
    void setLoginAction(ActionListener listener);
    void setRegisterAction(ActionListener listener);
    void setGuestAction(ActionListener listener);
}
