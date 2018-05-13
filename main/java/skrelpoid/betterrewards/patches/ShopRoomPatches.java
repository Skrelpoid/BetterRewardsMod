package skrelpoid.betterrewards.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.rooms.ShopRoom;

public class ShopRoomPatches {

	// @SpirePatch(cls = "com.megacrit.cardcrawl.rooms.ShopRoom", method =
	// "update")
	public static class UpdateEvent {
		public static void Prefix(Object o) {
			ShopRoom room = (ShopRoom) o;
			if (room.event != null) {
				room.event.update();
			}
		}
	}

	// @SpirePatch(cls = "com.megacrit.cardcrawl.rooms.ShopRoom", method =
	// "render")
	public static class RenderEvent {
		public static void Prefix(Object o1, Object o2) {
			ShopRoom room = (ShopRoom) o1;
			SpriteBatch sb = (SpriteBatch) o2;
			if (room.event != null) {
				room.event.render(sb);
			}
		}
	}

}
