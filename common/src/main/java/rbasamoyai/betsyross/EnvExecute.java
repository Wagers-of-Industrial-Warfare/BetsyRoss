package rbasamoyai.betsyross;

import java.util.function.Supplier;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class EnvExecute {

	@ExpectPlatform public static void executeOnClient(Supplier<Runnable> sup) { throw new AssertionError(); }

}
