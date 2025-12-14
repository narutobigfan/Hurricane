package haven;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CredentialsWidget extends Listbox<Label> {
    private static Text.Foundry font = new Text.Foundry(Text.mono.deriveFont(Font.PLAIN, UI.scale(14))).aa(true);
    private List<Label> usernames;
    @Override
    protected Label listitem(int i) {
        return usernames.get(i);
    }

    @Override
    protected int listitems() {
        return usernames.size();
    }

    @Override
    protected void drawitem(GOut g, Label item, int i) {
        g.text(item.texts, new Coord(20, 1));
        item.child.draw(g);
    }

    @Override
    public void change(Label username) {
        super.change(username);
        if(username != null && username.texts != null) {
            this.parent.wdgmsg("forget");
            this.parent.wdgmsg("login", new AuthClient.NativeCred(username.texts, getPassword(username.texts)), true);
        }
    }

    public void login(String username) {
        this.parent.wdgmsg("forget");
        this.parent.wdgmsg("login", new AuthClient.NativeCred(username, getPassword(username)), true);
    }

    static Coord scale = UI.scale(200, 300);

    public CredentialsWidget() {
        super(scale.x, 23, 20);
        init();
        this.bgcolor = new Color(0,0,0,128);
    }

    private void init() {
        this.usernames = new ArrayList<>();

        for(String username : Database.credentialsTable.getUsernames()) {
            Label lbl = new Label(username, font);

            Label xLabel = new Label("X", font) {
                @Override
                public void mousemove(Coord c) {
                    System.out.println("mouse move on " + username);
                    if (c.isect(Coord.z, sz)) {
                        this.setcolor(Color.RED);
                    } else if (this.col == Color.RED) {
                        this.setcolor(Color.WHITE);
                    }
                    super.mousemove(c);
                }

                @Override
                public boolean mousedown(Coord c, int button) {
                    System.out.println("mouse down x on " + username);
                    removeCredentials(username);
                    this.parent.add(new CredentialsWidget());
                    this.parent.destroy();
                    return true;
                }
            };
            xLabel.setcolor(Color.RED);

            lbl.add(xLabel);
            usernames.add(lbl);
        }
    }

    private void clearCredentials() {
        for(Iterator<Label> i = findchilds(Label.class).iterator(); i.hasNext();) {
            Label next = i.next();
            i.remove();
            next.destroy();
        }
    }

    public void reset() {
        clearCredentials();
        init();
    }

    public void saveCredentials(String username, String password) {
        Database.credentialsTable.saveCredentials(username, password);
    }

    public void removeCredentials(String username) {
        if(this.usernames != null) {
            for(Label label : usernames) {
                if(label.texts.equals(username)) {
                    this.usernames.remove(label);
                    label.destroy();
                    break;
                }
            }
        }
        Database.credentialsTable.removeCredentials(username);
    }

    public String getPassword(String username) {
        return Database.credentialsTable.getPassword(username);
    }
}
