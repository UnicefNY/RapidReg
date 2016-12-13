package org.unicef.rapidreg;

import com.google.gson.JsonObject;
import com.google.gson.internal.LazilyParsedNumber;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void retryWhenUsesRoot() {
        final String[] rootResult = {"root-stuff"};
        final String[] childResult = {"child-stuff"};

        final class Counter {
            private int _value = 0;

            public int next() {
                return ++_value;
            }

            public int current() {
                return _value;
            }
        }

        final Counter rootUsageCounter = new Counter();
        final Counter childUsageCounter = new Counter();
        final Counter childFailureCounter = new Counter();

        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {

                rootUsageCounter.next();
                System.out.println("Creating a root observable");
                return Observable.from(rootResult);

            }
        })
                .retry()
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String result) {

                        if (result.equals("root-stuff")) {
                            return Observable.defer(new Func0<Observable<String>>() {
                                @Override
                                public Observable<String> call() {

                                    childUsageCounter.next();
                                    System.out.println("Creating a child observable");
                                    if (childFailureCounter.next() < 3) {
                                        System.out.println("Child will issue a recoverable failure");
                                        return Observable.error(new RuntimeException());
                                    } else {
                                        System.out.println("Child will issue a result");
                                        return Observable.from(childResult);
                                    }

                                }
                            });
                        } else {
                            System.out.println("Child will issue an unrecoverable failure");
                            return Observable.error(new IllegalStateException());
                        }

                    }
                })
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Observable<? extends Throwable> attempts) {
                        return attempts.flatMap(new Func1<Throwable, Observable<Long>>() {
                            @Override
                            public Observable<Long> call(Throwable error) {

                                if (error instanceof RuntimeException) {
                                    System.out.println("retryWhen() returning a timer observable");
                                    return Observable.timer(1, TimeUnit.SECONDS);
                                } else {
                                    System.out.println("retryWhen() returning an error observable");
                                    return Observable.error(error);
                                }

                            }
                        });
                    }
                })
                .toBlocking()
                .forEach(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        System.out.println("Got a result: " + result);
                    }
                });

        // If the retryWhen() operator resubscribes to its parent Observable,
        // the root observable will only be subscribed-to once, and this test
        // will fail. If retryWhen() resubscribes to the root Observable, then
        // this test will succeed.
        assertTrue(rootUsageCounter.current() > 1);
    }

    @Test
    public void testA() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("a",3.6);
        System.out.println(jsonObject.get("a").getAsInt());
        System.out.println(Double.valueOf(new LazilyParsedNumber("3.6").toString()).longValue());
        System.out.println(Boolean.valueOf("TRu").toString());
    }
}
