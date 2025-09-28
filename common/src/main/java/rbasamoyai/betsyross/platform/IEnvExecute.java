package rbasamoyai.betsyross.platform;

import java.util.function.Supplier;

public interface IEnvExecute {
    void executeOnClient(Supplier<Runnable> sup);
}
