package com.github.cybellereaper.fusionBlock.nano;

import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NanoOutEventTest {

    @Test
    void cancellationCanBeToggled() {
        NanoOutEvent event = new NanoOutEvent();
        assertFalse(event.isCancelled());

        event.setCancelled(true);
        assertTrue(event.isCancelled());
    }

    @Test
    void handlersAreAccessible() {
        HandlerList handlerList = NanoOutEvent.getHandlerList();
        assertNotNull(handlerList);
        assertSame(handlerList, new NanoOutEvent().getHandlers());
    }
}
