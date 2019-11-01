package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Yiwen Feng
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (String s:rotors) {
            for (Rotor t:_allRotors) {
                if (t.name().equals(s)) {
                    myrotors.add(t);
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < numRotors() - 1; i++) {
            myrotors.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Make the rotor advance.*/
    void machineAdvance() {
        myrotors.get(numRotors() - 1).setneedrotate();
        for (int i = numRotors() - 1; i > numRotors() - numPawls(); i--) {
            if (myrotors.get(i).atNotch()) {
                myrotors.get(i).setneedrotate();
                myrotors.get(i - 1).setneedrotate();
            }
        }
        for (int i = numRotors() - 1; i >= numRotors() - numPawls(); i--) {
            if (myrotors.get(i).needrotate()) {
                myrotors.get(i).advance();
                myrotors.get(i).freenotch();
            }
        }
    }
    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int value;
        value = _plugboard.permute(c);
        machineAdvance();
        for (int i = myrotors.size() - 1; i >= 0; i--) {
            Rotor thisRotor = myrotors.get(i);
            value = thisRotor.convertForward(value);
        }
        for (int i = 1; i < myrotors.size(); i++) {
            Rotor thisRotor = myrotors.get(i);
            value = thisRotor.convertBackward(value);
        }
        value = _plugboard.permute(value);
        return value;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String ans = new String();
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                continue;
            }
            ans += _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return ans;
    }

    /** Reset the machine.*/
    void reset() {
        myrotors.clear();
        for (Rotor r:_allRotors) {
            r.clearused();
        }
    }

    /** Set the ring.
     * @param t  */
    void setRing(String t) {
        for (int i = 1; i < numRotors(); i++) {
            myrotors.get(i).setRing(t.charAt(i - 1));
        }
    }


    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of the rotors. */
    private int _numRotors;
    /** Number of the pawls. */
    private int _pawls;
    /** Collection of the available rotars. */
    private Collection<Rotor> _allRotors;
    /** Plugboard. */
    private Permutation _plugboard;
    /** Arraylist of my rotars. */
    private ArrayList<Rotor> myrotors = new ArrayList<>();

}
