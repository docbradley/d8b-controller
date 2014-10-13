/**
 * An "Actor" is a {@link java.util.concurrent.Callable} that can be
 * used to perform some defined task or operation.  An actor may be
 * short-lived (e.g., {@link com.adamdbradley.d8b.actor.ConsoleBooter})
 * or may persist once started (e.g., an HUI application).  Actors that
 * need to listen for events from the console can use
 * {@link com.adamdbradley.d8b.console.ConsoleControlConnection#subscribe(java.util.Queue)}
 * to subscribe to the parsed event queue.  A similar mechanism will be
 * added for audio events, but until then Actors can use
 * {@link com.adamdbradley.d8b.audio.AudioControlConnection}'s
 * {@link com.adamdbradley.d8b.ControlConnection#signalBuffer}
 * to find and consume messages.
 */
package com.adamdbradley.d8b.actor;
