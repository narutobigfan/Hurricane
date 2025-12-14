//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package haven.resutil;

import haven.Coord;
import haven.Coord3f;
import haven.MCache;
import haven.MapMesh;
import haven.Material;
import haven.MeshBuf;
import haven.Resource;
import haven.Surface;
import haven.Tiler;
import haven.Tileset;
import haven.MapMesh.Model;
import haven.Tiler.ResName;
import haven.render.Pipe;
import java.util.Objects;
import java.util.Random;

public class CaveTile extends Tiler {
	public static final float h = 3.0F;
	private static Coord3f tilesz;
	private static float py;
	public final Material wtex;
	public final Tiler ground;
	public static final MapMesh.DataID<Walls> walls;
	private static final Coord[] tces;
	private static final Coord[] tccs;

	public CaveTile(int id, Tileset set, Material wtex, Tiler ground) {
		super(id);
		this.wtex = wtex;
		this.ground = ground;
	}

	private void modelwall(Walls w, Coord ltc, Coord rtc) {
		Surface.Vertex[] lw = w.fortile(ltc), rw = w.fortile(rtc);
		for(int i = 0; i < lw.length - 1; i++) {
			w.ms.new Face(lw[i + 1], lw[i], rw[i + 1]);
			w.ms.new Face(lw[i], rw[i], rw[i + 1]);
		}
	}

	private void modelwallFlat(Walls w, Coord tc, boolean[] walled) {
		Surface.Vertex[] lw = w.fortileFlat(tc, 1.0F);
		MapMesh.MapSurface var10002 = w.ms;
		Objects.requireNonNull(var10002);
		w.ms.new Face(lw[0], lw[1], lw[2]);
		var10002 = w.ms;
		Objects.requireNonNull(var10002);
		w.ms.new Face(lw[0], lw[2], lw[3]);
	}

	public void model(MapMesh m, Random rnd, Coord lc, Coord gc) {
		super.model(m, rnd, lc, gc);
		Walls w = null;
		boolean[] walled = new boolean[4];

		for(int i = 0; i < 4; ++i) {
			int cid = m.map.gettile(gc.add(tces[i]));
			if (cid > this.id && !(m.map.tiler(cid) instanceof CaveTile)) {
				if (w == null) {
					w = (Walls)m.data(walls);
				}

				walled[i] = true;
			}
		}

		if (walled[0] || walled[1] || walled[2] || walled[3]) {
			this.modelwallFlat(w, lc, walled);
		}

	}

	private Pipe.Op mkWtex() {
		return this.wtex;
	}

	private void mkwall(MapMesh m, Walls w, Coord ltc, Coord rtc) {
		Surface.Vertex[] lw = w.fortile(ltc), rw = w.fortile(rtc);
		MapMesh.Model mod = MapMesh.Model.get(m, wtex);
		MeshBuf.Vertex[] lv = new MeshBuf.Vertex[lw.length], rv = new MeshBuf.Vertex[rw.length];
		MeshBuf.Tex tex = mod.layer(mod.tex);
		for(int i = 0; i < lv.length; i++) {
			float ty = (float)i / (float)(lv.length - 1);
			lv[i] = new Surface.MeshVertex(mod, lw[i]);
			tex.set(lv[i], new Coord3f(0, ty, 0));
			rv[i] = new Surface.MeshVertex(mod, rw[i]);
			tex.set(rv[i], new Coord3f(1, ty, 0));
		}
		for(int i = 0; i < lv.length - 1; i++) {
			mod.new Face(lv[i + 1], lv[i], rv[i + 1]);
			mod.new Face(lv[i], rv[i], rv[i + 1]);
		}
	}

	private void mkwallFlat(MapMesh m, Walls w, Coord tc) {
		Surface.Vertex[] lw = w.fortileFlat(tc, 1.0F);
		MapMesh.Model mod = Model.get(m, this.mkWtex());
		MeshBuf.Vertex[] lv = new MeshBuf.Vertex[20];
		MeshBuf.Tex tex = (MeshBuf.Tex)mod.layer(Model.tex);
		int i = 0;
		lv[i] = new Surface.MeshVertex(mod, lw[0]);
		tex.set(lv[i++], new Coord3f(0.0F, 1.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[1]);
		tex.set(lv[i++], new Coord3f(0.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[2]);
		tex.set(lv[i++], new Coord3f(1.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[3]);
		tex.set(lv[i++], new Coord3f(1.0F, 1.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[3]);
		tex.set(lv[i++], new Coord3f(0.0F, py, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[7]);
		tex.set(lv[i++], new Coord3f(0.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[4]);
		tex.set(lv[i++], new Coord3f(1.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[0]);
		tex.set(lv[i++], new Coord3f(1.0F, py, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[2]);
		tex.set(lv[i++], new Coord3f(0.0F, py, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[6]);
		tex.set(lv[i++], new Coord3f(0.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[7]);
		tex.set(lv[i++], new Coord3f(1.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[3]);
		tex.set(lv[i++], new Coord3f(1.0F, py, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[1]);
		tex.set(lv[i++], new Coord3f(0.0F, py, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[5]);
		tex.set(lv[i++], new Coord3f(0.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[6]);
		tex.set(lv[i++], new Coord3f(1.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[2]);
		tex.set(lv[i++], new Coord3f(1.0F, py, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[0]);
		tex.set(lv[i++], new Coord3f(0.0F, py, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[4]);
		tex.set(lv[i++], new Coord3f(0.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[5]);
		tex.set(lv[i++], new Coord3f(1.0F, 0.0F, 0.0F));
		lv[i] = new Surface.MeshVertex(mod, lw[1]);
		tex.set(lv[i++], new Coord3f(1.0F, py, 0.0F));

		for(int j = 0; j < 20; j += 4) {
			Objects.requireNonNull(mod);
			mod.new Face(lv[j], lv[j + 1], lv[j + 2]);
			Objects.requireNonNull(mod);
			mod.new Face(lv[j], lv[j + 2], lv[j + 3]);
		}

	}

	public void lay(MapMesh m, Random rnd, Coord lc, Coord gc) {
		Walls w = null;
		boolean[] walled = new boolean[4];

		for(int i = 0; i < 4; ++i) {
			int cid = m.map.gettile(gc.add(tces[i]));
			if (cid > this.id && !(m.map.tiler(cid) instanceof CaveTile)) {
				if (w == null) {
					w = (Walls)m.data(walls);
				}

				walled[i] = true;
			}
		}

		if (walled[0] || walled[1] || walled[2] || walled[3]) {
			this.mkwallFlat(m, w, lc);
		}

		if (this.ground != null) {
			this.ground.lay(m, rnd, lc, gc);
		}

	}

	public void trans(MapMesh m, Random rnd, Tiler gt, Coord lc, Coord gc, int z, int bmask, int cmask) {
	}

	static {
		tilesz = Coord3f.of((float)MCache.tilesz.x, (float)MCache.tilesz.y, 0.0F);
		py = 0.27272728F;
		walls = MapMesh.makeid(Walls.class);
		tces = new Coord[]{new Coord(0, -1), new Coord(1, 0), new Coord(0, 1), new Coord(-1, 0)};
		tccs = new Coord[]{new Coord(0, 0), new Coord(1, 0), new Coord(1, 1), new Coord(0, 1)};
	}

	public static class Walls {
		public final MapMesh m;
		public final MapMesh.Scan cs;
		public final Surface.Vertex[][] wv;
		private MapMesh.MapSurface ms;

		public Walls(MapMesh m) {
			this.m = m;
			this.ms = (MapMesh.MapSurface)m.data(MapMesh.gnd);
			this.cs = new MapMesh.Scan(Coord.z, m.sz.add(1, 1));
			this.wv = new Surface.Vertex[this.cs.l][];
		}

		public Surface.Vertex[] fortile(Coord tc) {
			if(wv[cs.o(tc)] == null) {
				Random rnd = m.grnd(tc.add(m.ul));
				Surface.Vertex[] buf = wv[cs.o(tc)] = new Surface.Vertex[4];
				buf[0] = ms.new Vertex(ms.fortile(tc));
				for(int i = 1; i < buf.length; i++) {
					buf[i] = ms.new Vertex(buf[0].x, buf[0].y, buf[0].z + (i * h / (buf.length - 1)));
					buf[i].x += (rnd.nextFloat() - 0.5f) * 3.0f;
					buf[i].y += (rnd.nextFloat() - 0.5f) * 3.0f;
					buf[i].z += (rnd.nextFloat() - 0.5f) * 3.5f;
				}
			}
			return(wv[cs.o(tc)]);
		}
		public Surface.Vertex[] fortileFlat(Coord tc, float mul) {
			if (this.wv[this.cs.o(tc)] == null) {
				Surface.Vertex[] buf = this.wv[this.cs.o(tc)] = new Surface.Vertex[8];
				MapMesh.MapSurface var10004 = this.ms;
				Objects.requireNonNull(var10004);
				MapMesh.MapSurface var10007 = this.ms;
				Objects.requireNonNull(var10007);
				buf[0] = ms.new Vertex(ms.fortile(tc).add(0.0F, 0.0F, 3.0F * mul));
				var10004 = this.ms;
				Objects.requireNonNull(var10004);
				buf[1] = ms.new Vertex(buf[0].add(0.0F, -CaveTile.tilesz.y, 0.0F));
				var10004 = this.ms;
				Objects.requireNonNull(var10004);
				buf[2] = ms.new Vertex(buf[0].add(CaveTile.tilesz.x, -CaveTile.tilesz.y, 0.0F));
				var10004 = this.ms;
				Objects.requireNonNull(var10004);
				buf[3] = ms.new Vertex(buf[0].add(CaveTile.tilesz.x, 0.0F, 0.0F));
				var10004 = this.ms;
				Objects.requireNonNull(var10004);
				buf[4] = ms.new Vertex(buf[0].add(0.0F, 0.0F, -3.0F * mul));
				var10004 = this.ms;
				Objects.requireNonNull(var10004);
				buf[5] = ms.new Vertex(buf[4].add(0.0F, -CaveTile.tilesz.y, 0.0F));
				var10004 = this.ms;
				Objects.requireNonNull(var10004);
				buf[6] = ms.new Vertex(buf[4].add(CaveTile.tilesz.x, -CaveTile.tilesz.y, 0.0F));
				var10004 = this.ms;
				Objects.requireNonNull(var10004);
				buf[7] = ms.new Vertex(buf[4].add(CaveTile.tilesz.x, 0.0F, 0.0F));
			}

			return this.wv[this.cs.o(tc)];
		}
	}

	@ResName("cave")
	public static class Factory implements Tiler.Factory {
		public Factory() {
		}

		public Tiler create(int id, Tileset set) {
			Material wtex = null;
			Tiler ground = null;
			Object[] var5 = set.ta;
			int var6 = var5.length;

			for(int var7 = 0; var7 < var6; ++var7) {
				Object rdesc = var5[var7];
				Object[] desc = (Object[])rdesc;
				String p = (String)desc[0];
				if (p.equals("wmat")) {
					wtex = ((Material.Res)set.getres().flayer(Material.Res.class, (Integer)desc[1])).get();
				} else if (p.equals("gnd")) {
					Resource gres = (Resource)set.getres().pool.load((String)desc[1], (Integer)desc[2]).get();
					Tileset ts = (Tileset)gres.flayer(Tileset.class);
					ground = ts.tfac().create(id, ts);
				}
			}

			return new CaveTile(id, set, wtex, ground);
		}
	}
}
