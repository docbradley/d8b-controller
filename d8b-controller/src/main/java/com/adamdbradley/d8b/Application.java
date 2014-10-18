package com.adamdbradley.d8b;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.adamdbradley.d8b.actor.Actor;
import com.adamdbradley.d8b.audio.AudioControlConnection;
import com.adamdbradley.d8b.console.ConsoleControlConnection;
import com.adamdbradley.d8b.console.signal.Signal;

public abstract class Application
implements Runnable {

    protected final ScheduledExecutorService executor = Executors.newScheduledThreadPool(16);

    protected volatile boolean shutdown = false;

    protected final ConsoleControlConnection console;
    protected final AudioControlConnection audio;
    protected final Queue<Signal> consoleSignals = new ConcurrentLinkedQueue<>();

    protected Application(final ConsoleControlConnection console,
            final AudioControlConnection audio) {
        this.console = console;
        this.audio = audio;
        console.subscribe(consoleSignals);
    }

    protected void waitFor(final Actor actor) {
        waitFor(executor.submit(actor));
    }

    protected void waitFor(final Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Java chooses which method to invoke based on the type of the
     * REFERENCE at the invocation point; e.g, if I have methods
     * <code>foo(SuperClass)</code> and <code>foo(SubClass)</code>,
     * then:
     * <pre>
     *   SuperClass c = new SubClass();
     *   foo(c);
     * </pre>
     * will invoke the <code>foo(SuperClass)</code> method.
     * <p/>
     * This method allows you to instead invoke the tightest-fitting
     * existing method for the parmeter type, enabling a type-keyed
     * dispatch pattern.
     * @param object     The object on which to invoke the method
     * @param methodName The name of the method(s)
     * @param parameters Non-null parameters to pass to method on object
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T> T typedDispatch(final Object object,
            final String methodName,
            final Object ... parameters) {
        final Class<?>[] parameterTypes = new Class<?>[parameters.length];
        for (int i=0; i<parameters.length; i++) {
            if (parameters[i] == null) {
                throw new NullPointerException("null params not supported in typedDispatch");
            }
            parameterTypes[i] = parameters[i].getClass();
        }
        try {
            final Method method = this.getClass().getMethod(methodName, parameterTypes);
            return (T) method.invoke(this, parameters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No method found for "
                    + methodName + " " + Arrays.asList(parameterTypes)
                    + "; consider creating a least-common-type catch-all?", e);
        } catch (SecurityException | IllegalAccessException  | IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

}
