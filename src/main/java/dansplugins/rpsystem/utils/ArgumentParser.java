package dansplugins.rpsystem.utils;

public class ArgumentParser {

    private static ArgumentParser instance;

    private ArgumentParser() {

    }

    public static ArgumentParser getInstance() {
        if (instance == null) {
            instance = new ArgumentParser();
        }
        return instance;
    }

    public String createStringFromFirstArgOnwards(String[] args, int startingArg) {
        StringBuilder name = new StringBuilder();
        for (int i = startingArg; i < args.length; i++) {
            name.append(args[i]);
            if (!(i == args.length - 1)) {
                name.append(" ");
            }
        }
        return name.toString();
    }

    public String createStringFromArgs(String[] args) {
        String toReturn = args[0];
        for (int i = 1; i < args.length; i++) {
            toReturn = toReturn + " " + args[i];
        }
        return toReturn;
    }

}
