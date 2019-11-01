package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Yiwen Feng
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }


    /** comment.
     * @return
     * @param s  */
    boolean isSetting(String s) {
        if (nextValue(s) == '*') {
            return true;
        }
        return false;
    }

    /** comment.
     * @param m */
    public void removefirst(String[] m) {
        System.arraycopy(m, 1, m, 0, m.length - 1);
    }

    /** commit.
     * @param mymachine fsd
     * @param s dfs
     * @param names */
    public void set(Machine mymachine, String s, ArrayList<String> names) {
        String[] msg = s.split("\\s+");
        int msgpos = 0;
        find: for (int i = 1; i <= mymachine.numRotors(); i++) {
            for (Rotor r:_allRotors) {
                if (msg[i].equals(r.name())) {
                    if (!r.isUsed()) {
                        if (i == 0) {
                            if (!(msg[i].equals("B") || msg[i].equals("C"))) {
                                throw error("First rotor is not a reflector.");
                            }
                        }
                        names.add(msg[i]);
                        r.setisUsed();
                        continue find;
                    } else {
                        throw error("A rotor is repeated in the setting line.");
                    }
                }
            }
            throw error("The rotors is misnamed.");
        }
        String[] namearray = new String[names.size()];
        for (int i = 0; i < names.size(); i++) {
            namearray[i] = names.get(i);
        }
        mymachine.insertRotors(namearray);
        msgpos += mymachine.numRotors() + 1;
        mymachine.setRotors(msg[msgpos]);
        msgpos++;
        if (msgpos < msg.length) {
            if (nextValue(msg[msgpos]) != '(') {
                mymachine.setRing(msg[msgpos]);
                msgpos++;
            }
        }
        String plug = "";
        for (int i = msgpos; i < msg.length; i++) {
            plug += msg[i];
        }
        mymachine.setPlugboard(new Permutation(plug, _alphabet));
    }
    /** comment. */
    private void process() {
        Machine mymachine = readConfig();
        String read = _input.nextLine();
        ArrayList<String> names = new ArrayList<String>();
        if (!isSetting(read)) {
            throw error("The input might not start with a setting.");
        }
        while (true) {
            if (isSetting(read)) {
                mymachine.reset();
                names.clear();
                set(mymachine, read, names);
            } else {
                printMessageLine(mymachine.convert(read));
            }
            if (_input.hasNextLine()) {
                read = _input.nextLine();
            } else {
                break;
            }
        }
    }

    /** comment.
     * @return
     * @param s */
    public char nextValue(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') {
                return s.charAt(i);
            }
        }
        return 0;
    }
    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine());
            int numRotors = _config.nextInt();
            int palws = _config.nextInt();
            while (_config.hasNext()) {
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, palws, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String message = _config.next();
            String cycle = _config.nextLine();
            while (_config.hasNext("[(].+")) {
                cycle += _config.nextLine();
            }
            Permutation permu = new Permutation(cycle, _alphabet);
            if (message.charAt(0) == 'M') {
                String notch = message.substring(1);
                return  new MovingRotor(name, permu, notch);
            } else if (message.charAt(0) == 'N') {
                return new FixedRotor(name, permu);
            } else {
                return new Reflector(name, permu);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            if (i % 5 == 4 && i != msg.length() - 1) {
                _output.print(" ");
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** comment. */
    private Collection<Rotor> _allRotors = new ArrayList<>();
}
