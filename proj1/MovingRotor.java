package enigma;


import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Yiwen Feng
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */

    protected String _notches;
    /** process.
     * @param notches ferf
     * @param perm ferf
     * @param name gfsd*/
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
        _setting = 0;
        _needrotate = false;
    }

    @Override
    void advance() {
        if (setting() != alphabet().size() - 1) {
            _setting++;
        } else {
            _setting = 0;
        }
    }

    /** comment.
     * @return  */

    boolean needrotate() {
        return _needrotate;
    }

    /** comment. */
    void setneedrotate() {
        _needrotate = true;
    }

    /** comment. */
    void freenotch() {
        _needrotate = false;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            char x = _notches.charAt(i);
            if (x == permutation().alphabet().toChar(setting())) {
                return true;
            }
        }
        return false;
    }

    @Override
    boolean rotates() {
        return true;
    }

    /** comment. */
    private boolean _needrotate;

}
