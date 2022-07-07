package sample.samplespec;

/***************************************************************************
 * This file was auto-generated on Tuesday, 05 July 2022 at 10:06:30.
 * Please do not edit manually!
 **************************************************************************/

public class PEvents {
    public static class addEvent extends prt.events.PEvent<Integer> {
        public addEvent(int p) { this.payload = p; }
        private int payload;
        public Integer getPayload() { return payload; }

        @Override
        public String toString() { return "addEvent[" + payload + "]"; }
    } // addEvent

    public static class mulEvent extends prt.events.PEvent<Integer> {
        public mulEvent(int p) { this.payload = p; }
        private int payload;
        public Integer getPayload() { return payload; }

        @Override
        public String toString() { return "mulEvent[" + payload + "]"; }
    } // mulEvent

}
