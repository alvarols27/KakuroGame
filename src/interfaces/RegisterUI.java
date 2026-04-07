package interfaces;

import java.awt.event.ActionListener;

public interface RegisterUI {
    String getUsername();
    String getPassword();
    String getConfirmPassword();
    void showError(String message);
    void showSuccess(String message);
    void close();
    void setRegisterAction(ActionListener listener);
    void setBackAction(ActionListener listener);
}