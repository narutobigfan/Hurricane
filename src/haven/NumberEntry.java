package haven;
import haven.ReadLine;
import haven.TextEntry;

import java.awt.event.KeyEvent;

public class NumberEntry extends TextEntry {
    private final Integer min;
    private final Integer max;

    public NumberEntry(int w, Integer defnum, int min, int max) {
        super(w, defnum.toString());
        this.min = min;
        this.max = max;
    }

    @Override
    public void settext(String text) {
        if (isNumber(text)) {
            super.settext(text);
        }
    }

    @Override
    public boolean keydown(KeyEvent e) {
        char c = e.getKeyChar();
        if (isSpecialKeyCode(e) || isLeadingMinusSign(c) || Character.isDigit(c)) {
            return super.keydown(e);
        } else {
            return false;
        }
    }

    @Override
    public void commit() {
        if (isEmpty(text())) {
            settext(0 + "");
        }
        buf.point(buf.length());
        //dirty = false;
        redraw();
    }

    @Override
    protected void changed() {
        super.changed();
        int value = value();
        if (value < min) {
            setValue(min);
        } else if (value > max) {
            setValue(max);
        }
    }

    @Override
    public void done(ReadLine buf) {
        commit();
        super.done(buf);
    }

    public int value() {
        String text = text();
        if (isEmpty(text)) {
            return 0;
        } else {
            return Integer.parseInt(text);
        }
    }

    public void setValue(Integer value) {
        settext(value.toString());
        commit();
    }

    private boolean isSpecialKeyCode(KeyEvent ev) {
        int code = ev.getKeyCode();
        return code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_ENTER || code == KeyEvent.VK_TAB || code == KeyEvent.VK_ESCAPE;
    }

    private boolean isLeadingMinusSign(char c) {
        String text = text();
        return buf.point() == 0 && c == '-' && (text.isEmpty() || text().matches("\\d+"));
    }

    private boolean isNumber(String text) {
        return text.matches("-?(0|[1-9]\\d*)");
    }

    private boolean isEmpty(String text) {
        return text.isBlank() || text.equals("-");
    }
}
