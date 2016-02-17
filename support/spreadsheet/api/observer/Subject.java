package spreadsheet.api.observer;

/**
 * Created by ab8015 on 17/02/16.
 */
public interface Subject{

    public void register (Observer newobserver);

    public void unregister (Observer toremoveobserver);

    public void notifyObservers();

}
