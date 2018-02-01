package org.streampipes.manager;

/**
 * Helper class to assert exceptions
 * See http://www.codeaffine.com/2014/07/28/clean-junit-throwable-tests-with-java-8-lambdas/
 */
public class ThrowableCaptor {

    public interface Actor {
        void act() throws Throwable;
    }

    public static Throwable captureThrowable( Actor actor ) {
        Throwable result = null;
        try {
            actor.act();
        } catch( Throwable throwable ) {
            result = throwable;
        }
        return result;
    }


}
