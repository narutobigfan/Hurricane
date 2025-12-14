package haven.automated;


import com.google.gson.Gson;
import haven.*;
import haven.Button;
import haven.Composite;
import haven.Frame;
import haven.Label;
import haven.Window;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RouteWindow extends Window {
    private final Gson gson = new Gson();
    private Route currentRoute;
    private final CurrentRouteWidget currentRouteWidget;
    private Frame frame;
    private final TextEntry routeNameEntry;
    private final TextEntry accountEntry;
    private final TextEntry characterEntry;
    private final TextEntry discordMessageEntry;
    private final TextEntry discordChannelEntry;
    private final TextEntry targetNameEntry;
    private final CheckBox hearthBackCheckbox;
    private final CheckBox pickupCheckbox;
    private final NumberEntry reloginTimeEntry;
    public ArrayList<LoginRoute> loginRoutes = new ArrayList<>();

    public RouteWindow() {
        super(Coord.z, "Route");

        Widget prev;
        prev = add(new Button(UI.scale(180), "Save waypoint").action(() -> {
            saveCoord();
        }), 0, 0);

        prev = add(new Label("Account:"), prev.pos("bl").adds(0, 5));
        prev = accountEntry = add(new TextEntry(UI.scale(180), ""), prev.pos("bl").adds(0, 5));

        prev = add(new Label("Character:"), prev.pos("bl").adds(0, 5));
        prev = characterEntry = add(new TextEntry(UI.scale(180), ""), prev.pos("bl").adds(0, 5));

        prev = add(new Label("Discord message:"), prev.pos("bl").adds(0, 5));
        prev = discordMessageEntry = add(new TextEntry(UI.scale(180), "Found angler :)"), prev.pos("bl").adds(0, 5));

        prev = add(new Label("Discord channel:"), prev.pos("bl").adds(0, 5));
        prev = discordChannelEntry = add(new TextEntry(UI.scale(180), ""), prev.pos("bl").adds(0, 5));

        prev = add(new Label("Target name:"), prev.pos("bl").adds(0, 5));
        prev = targetNameEntry = add(new TextEntry(UI.scale(180), ""), prev.pos("bl").adds(0, 5));

        prev = add(new Label("Route:"), prev.pos("bl").adds(0, 5));
        prev = routeNameEntry = add(new TextEntry(UI.scale(180), "route"), prev.pos("bl").adds(0, 5));

        prev = add(new Label("Relogin time:"), prev.pos("bl").adds(0, 5));
        prev = reloginTimeEntry = add(new NumberEntry(UI.scale(180), 20, 1, 60), prev.pos("bl").adds(0, 5));

        prev = hearthBackCheckbox = add(new CheckBox("Hearth back") {
            {a = false;}

            public void set(boolean val) {
                a = val;
            }
        }, prev.pos("bl").adds(0, 5));

        prev = pickupCheckbox = add(new CheckBox("Pick up") {
            {a = false;}

            public void set(boolean val) {
                a = val;
            }
        }, prev.pos("bl").adds(0, 5));

        prev = add(new Button(UI.scale(180), "Save route").action(() -> {
            String routeName = routeNameEntry.text();
            try (Writer writer = new FileWriter(Config.HOMEDIR.getPath() + "/" + routeName + ".json")) {
                LoginRoute lr = new LoginRoute(accountEntry.text(), characterEntry.text(), discordChannelEntry.text(), targetNameEntry.text(), discordMessageEntry.text(), hearthBackCheckbox.a, pickupCheckbox.a, currentRoute);
                gson.toJson(lr, writer);
            } catch (IOException ex) {
            }
        }), prev.pos("bl").adds(0, 5));

        prev = add(new Button(UI.scale(180), "Load route").action(() -> {
            String routeName = routeNameEntry.text();
            try {
                LoginRoute lr = gson.fromJson(new FileReader(Config.HOMEDIR.getPath() + "/" + routeName + ".json"), LoginRoute.class);
                currentRoute = lr.route;
                accountEntry.settext(lr.accountName);
                characterEntry.settext(lr.characterName);
                discordChannelEntry.settext(lr.discordChannel);
                discordMessageEntry.settext(lr.discordMessage);
                targetNameEntry.settext(lr.targetName);
                hearthBackCheckbox.set(lr.hearthBack);
                pickupCheckbox.set(lr.pickup);
                updateRoute();
            } catch (FileNotFoundException ex) {
            }
        }), prev.pos("bl").adds(0, 5));

        prev = add(new Button(UI.scale(180), "Add route").action(() -> {
            String routeName = routeNameEntry.text();
            try {
                LoginRoute lr = gson.fromJson(new FileReader(Config.HOMEDIR.getPath() + "/" +  routeName + ".json"), LoginRoute.class);
                loginRoutes.add(lr);
                sysMsg(ui, "Route added", Color.WHITE);
            } catch (FileNotFoundException ex) {
            }
        }), prev.pos("bl").adds(0, 5));

        prev = add(new Button(UI.scale(180), "Run").action(() -> {
            if (loginRoutes.isEmpty()) {
                sysMsg(ui, "No routes", Color.WHITE);
                return;
            }

            Thread runner = new Thread(new Runner(ui, loginRoutes, reloginTimeEntry.value()), "Current Route");
            sysMsg(ui, "Starting route", Color.WHITE);
            runner.start();
        }), prev.pos("bl").adds(0, 5));

        currentRouteWidget = add(new CurrentRouteWidget(currentRoute), prev.pos("bl").adds(0, 5));
        drawFrame();
        /*justclose = true;*/
        pack();
    }

    private void drawFrame() {
        if (currentRoute != null) {
            if (frame != null) {
                frame.destroy();
            }

            frame = Frame.around(this, Collections.singletonList(currentRouteWidget));
        }
    }

    private void saveCoord() {
        Coord2d playerCoord = ui.gui.map.player().rc;
        Coord playerGridCoord = playerCoord.floor(MCache.tilesz);
        MCache.Grid playerGrid = ui.sess.glob.map.getgridt(playerGridCoord);
        Coord coord = playerGridCoord.sub(playerGrid.gc.mul(MCache.cmaps));

        if (currentRoute == null) {
            currentRoute = new Route();
        }

        currentRoute.addWaypoint(new Waypoint(playerGrid.id, coord));
        updateRoute();
    }

    private void updateRoute() {
        currentRouteWidget.updateRoute(currentRoute);
        drawFrame();
        pack();
    }

    private class Runner implements Runnable {
        private UI ui;
        ArrayList<LoginRoute> loginRoutes;
        private final int reloginTime;

        public Runner(UI ui, ArrayList<LoginRoute> loginRoutes, int reloginTime) {
            this.ui = ui;
            this.loginRoutes = loginRoutes;
            this.reloginTime = reloginTime;
        }

        private final Object LOCK = new Object();

        public void sleep(int timeout) {
            try {
                synchronized (LOCK) {
                    LOCK.wait(timeout);
                }
            } catch (InterruptedException ex) {
            }
        }

        @Override
        public void run() {
            if (loginRoutes.size() != 1) {
                while (true) {
                    for (LoginRoute lr : loginRoutes) {
                        try {
                            sysMsg(ui, "Running now: " + lr.accountName, Color.WHITE);
                            login(lr);

                            if (lr.pickup) {
                                runPickup(lr);
                            } else if (lr.hearthBack) {
                                runHearthFindTarget(lr);
                            } else {
                                runWalkFindTarget(lr);
                            }
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }

                    if (ui != null) {
                        ui.sess.close();
                    }
                    sysMsg(ui, reloginTime + " minutes left until next run.\n", Color.WHITE);
                    for (int i = 1; i <= reloginTime; i++) {
                        sleep(1000 * 60);
                        sysMsg(ui, reloginTime - i + " minutes left until next run.\n", Color.WHITE);
                    }
                }
            } else {
                LoginRoute lr = loginRoutes.get(0);
                try {
                    if (lr.pickup) {
                        runPickup(lr);
                    } else if (lr.hearthBack) {
                        runHearthFindTarget(lr);
                    } else {
                        runWalkFindTarget(lr);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        public void login(LoginRoute lr){
            if (ui != null) {
                ui.sess.close();
            }
            sleep(10000);
            LoginScreen.instance.login(lr.accountName);
            sleep(10000);
            Charlist.selectChar(lr.characterName);
            sleep(10000);
            ui = UI.instance;
        }

        private boolean waitForMovement(Gob gob, int timeout) {
            while (!isMoving(gob) && timeout > 0) {
                sleep(50);
                timeout -= 50;
            }

            return isMoving(gob);
        }

        public boolean isMoving(Gob gob) {
            Moving m = gob.getattr(Moving.class);
            return m != null;
        }

        private boolean findTarget(String name) {
            synchronized (ui.sess.glob.oc) {
                for (Gob gob : ui.sess.glob.oc) {
                    try {
                        Resource res = gob.getres();
                        if (res != null && res.name.equals(name)) {
                            return true;
                        }
                    } catch (Loading l) {
                    }
                }
            }

            return false;
        }

        private void checkRoad(Gob me, int buttonCount) {
            String milestoneResName = "gfx/terobjs/road/milestone-stone-e";
            Gob milestoneGob = null;
            synchronized (ui.sess.glob.oc) {
                for (Gob gob : ui.sess.glob.oc) {
                    try {
                        Resource res = gob.getres();
                        if (res != null && res.name.equals(milestoneResName)) {
                            double dist = me.rc.dist(gob.rc);
                            if (dist < 55) {
                                milestoneGob = gob;
                            }
                        }
                    } catch (Loading l) {
                    }
                }
            }

            for (int i = 0; i < buttonCount; i++) {
                ui.gui.map.wdgmsg("click", Coord.z, milestoneGob.rc.floor(OCache.posres), 3, 0, 1, (int) milestoneGob.id, milestoneGob.rc.floor(OCache.posres), -1, -1);
                sleep(1000);
                Optional<Window> milestoneWindowOpt =  ui.root.findchildren(Window.class).stream()
                        .filter(window -> "Milestone".equals(window.cap))
                        .findFirst();

                if (milestoneWindowOpt.isPresent()) {
                    Window milestoneWindow = milestoneWindowOpt.get();
                    List<Button> buttons = milestoneWindow.findchildren(Button.class).stream().filter(button -> "Travel".equals(button.text.text)).collect(Collectors.toList());

                    if (!buttons.isEmpty()) {
                        Button travelButton = buttons.get(i);
                        travelButton.click();
                        sleep(7000);
                        ui.gui.map.wdgmsg("click", Coord.z, Coord.z, 3, 0);
                        sleep(1000);
                    }
                }
            }
        }

        private boolean findDanger(Gob me) {
            synchronized (ui.sess.glob.oc) {
                for (Gob gob : ui.sess.glob.oc) {
                    try {
                        Resource res = gob.getres();
                        if (res != null && (res.name.equals("gfx/kritter/wolf/wolf") ||
                                res.name.equals("gfx/kritter/goat/wildgoat") ||
                                res.name.equals("gfx/kritter/adder/adder") ||
                                res.name.equals("gfx/kritter/goldeneagle/goldeneagle"))) {
                            double dist = me.rc.dist(gob.rc);
                            if (dist < 275) {
                                Drawable d = gob.getattr(Drawable.class);
                                if (d != null && d instanceof Composite) {
                                    Composite cpst = (Composite) d;
                                    boolean knocked = false;
                                    if (cpst.nposes != null && cpst.nposes.size() > 0) {
                                        for (ResData resdata : cpst.nposes) {
                                            Resource posres = resdata.res.get();
                                            if (posres.name.endsWith("/knock")) {
                                                knocked = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (knocked) {
                                        continue;
                                    }
                                }
                                return true;
                            }
                        }
                    } catch (Loading l) {
                    }
                }
            }

            return false;
        }

        private void runWalkFindTarget(LoginRoute lr) {
            Route currentRoute = lr.route;
            final Gob me = ui.sess.glob.oc.getgob(ui.gui.map.plgob);
            Gob mount = null;
            if (me != null && me.heldby > 0) {
                mount = ui.sess.glob.oc.getgob(me.heldby);
            } else {
                return;
            }

            Stack<Waypoint> completedWaypoints = new Stack<>();

            int i = 0;
            int total = currentRoute.waypoints.size();

            for (Waypoint waypoint : currentRoute.waypoints) {
                if (findTarget(lr.targetName)) {
                    DiscordHelper.alert(lr.discordChannel, lr.discordMessage + " (" + lr.characterName + "); Waypoint " + i + "/" + total);
                    break;
                }

                waypoint.moveTo(ui);
                waitForMovement(mount, 2500);

                while (isMoving(mount)) {
                    sleep(500);
                }

                completedWaypoints.push(waypoint);
                i++;
            }

            // Go in reverse direction
            while (!completedWaypoints.isEmpty()) {
                Waypoint reverseWaypoint = completedWaypoints.pop();
                reverseWaypoint.moveTo(ui); // Assuming moveTo can be used to go back

                waitForMovement(mount, 2500);
                while (isMoving(mount)) {
                    sleep(500);
                }
            }
        }

        private void runHearthFindTarget(LoginRoute lr) {
            Route currentRoute = lr.route;
            final Gob me = ui.sess.glob.oc.getgob(ui.gui.map.plgob);
            Gob snekja = null;
            if (me != null && me.heldby > 0) {
                snekja = ui.sess.glob.oc.getgob(me.heldby);
            } else {
                snekja = me;
            }

            int i = 0;
            int total = currentRoute.waypoints.size();

            for (Waypoint waypoint : currentRoute.waypoints) {
                if (findTarget(lr.targetName)) {
                    DiscordHelper.alert(lr.discordChannel, lr.discordMessage + " (" + lr.characterName + "); Waypoint " + i + "/" + total);
                    break;
                }

                if (findDanger(me)) {
                    break;
                }

                waypoint.moveTo(ui);
                waitForMovement(snekja, 2500);

                while (isMoving(snekja)) {
                    sleep(500);
                }

                i++;
            }

            ui.gui.menu.wdgmsg("act", "travel", "hearth", 0);
            sleep(10000);
        }

        private boolean findPickup(String name) {
            Gob closests = null;
            Coord2d playerCoord = ui.gui.map.player().rc;
            synchronized (ui.sess.glob.oc) {
                for (Gob gob : ui.sess.glob.oc) {
                    try {
                        Resource res = gob.getres();
                        if (res != null && res.name.equals(name)) {
                            if (closests == null || gob.rc.dist(playerCoord) < closests.rc.dist(playerCoord))
                                closests = gob;
                        }
                    } catch (Loading l) {
                    }
                }
            }

            if (closests == null || closests.rc.dist(playerCoord) > 11 * 20)
                return false;

            ui.gui.map.wdgmsg("click", ui.gui.map.sz.div(2), closests.rc.floor(OCache.posres), 3, 1, 0, (int) closests.id, closests.rc.floor(OCache.posres), 0, -1);
            sleep(5000);
            return true;
        }

        private void runPickup(LoginRoute lr) {
            Route currentRoute = lr.route;
            final Gob me = ui.sess.glob.oc.getgob(ui.gui.map.plgob);
            Gob mount = null;
            if (me != null && me.heldby > 0) {
                mount = ui.sess.glob.oc.getgob(me.heldby);
            } else {
                mount = me;
            }

            for (Waypoint waypoint : currentRoute.waypoints) {
                waypoint.moveTo(ui);
                waitForMovement(mount, 2500);

                while (isMoving(mount)) {
                    sleep(50);
                }

                boolean foundPickup = findPickup(lr.targetName);

                if (foundPickup) {
                    waypoint.moveTo(ui);
                    waitForMovement(mount, 2500);

                    while (isMoving(mount)) {
                        sleep(50);
                    }
                }
            }
        }
    }

    public class CurrentRouteWidget extends Widget {
        public Route route;

        public CurrentRouteWidget(Route route) {
            updateRoute(route);
        }

        public void updateRoute(Route route) {
            this.route = route;
            drawWaypoints();
        }

        private void drawWaypoints() {
            clear();

            if (route != null) {
                Widget prev = null;
                Scrollport scroll = add(new Scrollport(UI.scale(new Coord(200, 200))), 0, 0);
                Widget cont = scroll.cont;
                for (Waypoint waypoint : route.waypoints) {
                    prev = cont.add(new Label(waypoint.gridId + " " + waypoint.coord.toString()), prev == null ? Coord.z : prev.pos("bl").adds(0, 5));
                    prev = cont.add(new Button(UI.scale(180), "Move").action(() -> {
                        waypoint.moveTo(ui);
                    }), prev.pos("bl").adds(0, 5));
                    prev = cont.add(new Button(UI.scale(180), "Remove").action(() -> {
                        removeWaypoint(waypoint);
                    }), prev.pos("bl").adds(0, 5));
                }
            }

            pack();
        }

        private void removeWaypoint(Waypoint waypoint) {
            if (route != null) {
                route.removeWaypoint(waypoint);
                drawWaypoints();
            }
        }

        private void clear() {
            for (Iterator<Widget> i = children().iterator(); i.hasNext();) {
                Widget next = i.next();
                i.remove();
                next.destroy();
            }
        }

        public void drawbg(GOut g) {
            g.chcolor(0, 0, 0, 128);
            g.frect(Coord.z, sz);
            g.chcolor();
        }

        public void draw(GOut g) {
            drawbg(g);
            super.draw(g);
        }
    }

    public class Waypoint {
        public long gridId;
        public Coord coord;

        public Waypoint(long gridId, Coord coord) {
            this.gridId = gridId;
            this.coord = coord;
        }

        public void moveTo(UI ui) {
            MCache.Grid grid = ui.sess.glob.map.getgridbyid(gridId);
            if (grid != null) {
                Coord actualCoord = coord.add(grid.gc.mul(MCache.cmaps)).mul(MCache.tilesz).add(MCache.tilesz.div(2)).floor(OCache.posres);
                ui.gui.map.wdgmsg("click", Coord.z, actualCoord, 1, 0, 0);
            }
        }
    }

    public static class DiscordHelper {

        public static void alert(String channelId, String text) {
            String token = OptWnd.discordToken.buf.line();
            String api = OptWnd.discordAPI.buf.line() + "/channels/{0}/messages";
            System.out.println("Discord message: " + text);
            try {
                URL url = new URL(MessageFormat.format(api, channelId));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bot " + token);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = "{\"content\":\"" + text + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                System.out.println("HTTP Response Code: " + responseCode);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public class LoginRoute {
        public String accountName;
        public String characterName;
        public String discordChannel;
        public String discordMessage;
        public String targetName;
        public boolean hearthBack;
        public boolean pickup;
        public Route route;

        public LoginRoute(String accountName, String characterName, String discordChannel, String targetName, String discordMessage,  boolean hearthBack, boolean pickup, Route route) {
            this.accountName = accountName;
            this.characterName = characterName;
            this.discordChannel = discordChannel;
            this.discordMessage = discordMessage;
            this.targetName = targetName;
            this.hearthBack = hearthBack;
            this.pickup = pickup;
            this.route = route;
        }
    }

    public class Route {
        public List<Waypoint> waypoints = new ArrayList<>();

        public void addWaypoint(Waypoint waypoint) {
            this.waypoints.add(waypoint);
        }

        public void removeWaypoint(Waypoint waypoint) {
            this.waypoints.remove(waypoint);
        }
    }
    public static void sysMsg(UI ui, String str, Color col) {
        if (ui.gui != null)
            ui.gui.msg(str, col);
    }
}
