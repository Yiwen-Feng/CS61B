package enigma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Yiwen Feng
 *
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _ring = alphabet().toChar(0);
        String t;
        Pattern p = Pattern.compile("(?<=\\()(.+?)(?=\\))");
        Matcher m = p.matcher(cycles);
        if (m.find()) {
            do {
                t = m.group();
                addCycle(t);
                _numc++;
            } while (m.find());
        } else {
            for (int i = 0; i < size(); i++) {
                _cyclearr[i] = new char[2];
                _cyclearr[i][0] = _cyclearr[i][1] = _alphabet.toChar(i);
                _numc++;
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i++) {
            if (cycle.charAt(i) == ' ') {
                cycle = cycle.substring(0, i) + cycle.substring(i + 1);
                i--;
            } else if (!alphabet().contains(cycle.charAt(i))) {
                throw error("Charactar is not in alphbet.");
            }
        }
        cycle += cycle.charAt(0);
        _cyclearr[_numc] = cycle.toCharArray();
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }


    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int index = wrap(p);
        char c = _alphabet.toChar(index);
        return _alphabet.toInt(permute(c));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int index = wrap(c);
        char ch = _alphabet.toChar(index);
        return _alphabet.toInt(invert(ch));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        for (int i = 0; i < _numc; i++) {
            for (int j = 0; j < _cyclearr[i].length - 1; j++) {
                if (_cyclearr[i][j] == p) {
                    return _cyclearr[i][j + 1];
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        for (int i = 0; i < _numc; i++) {
            for (int j = 1; j < _cyclearr[i].length; j++) {
                if (_cyclearr[i][j] == c) {
                    return _cyclearr[i][j - 1];
                }
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _numc; i++) {
            if (_cyclearr[i].length == 2) {
                return false;
            }
        }
        return true;
    }

    /** set ring.
     * @param r */
    void setRing(char r) {
        _ring = r;
    }

    /** get ring.
     * @return */
    char getRing() {
        return _ring;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Alphabet of this permutation. */
    private char[][] _cyclearr = new char[100][];
    /** Alphabet of this permutation. */
    private int _numc;
    /** remark the ring. */
    private char _ring;
}
