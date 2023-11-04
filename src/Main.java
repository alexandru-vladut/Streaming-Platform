import java.io.IOException;

public class Main {
    static final int TEN = 10;
    static final int TWO = 2;
    static final int FIVE = 5;

    public static void main(final String[] args) throws IOException {

        for (int fileIndex = 1; fileIndex <= 10; fileIndex++) {

            Server.readFromJSON("testCases/in/basic_" + fileIndex + ".json");
            Server.resetCurrentInfo();

            Server.iterateThroughActions();

            Server.writeToJSON("testCases/out/basic_" + fileIndex + ".json");
        }
    }
}


