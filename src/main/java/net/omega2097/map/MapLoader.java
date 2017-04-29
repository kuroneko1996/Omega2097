package net.omega2097.map;
import net.omega2097.Image;
import net.omega2097.util.IRandom;
import net.omega2097.util.Util;


public class MapLoader {
    IRandom random;

    public MapLoader(IRandom random) {
        this.random = random;
    }

    public Map load(String fileName) {
        Image image = new Image(fileName);
        int[] pixels = image.bufferToIntArray();


        int width = image.getWidth();
        int height = image.getHeight();

        Map map = new Map(random);
        map.init(width, height);

        for (int x = 0; x < width; x++ ) {
            for (int y = 0; y < height; y++) {
                int color = pixels[x * width + y];
                map.setProperties(x, y, true, true);

                switch (color) {
                    case 0x808080:
                        map.setProperties(x, y, false, false); // wall
                        break;
                    case 0x0026FF:
                        map.addWall(x, y, "textures/walls/walls.png", 0);
                        break;
                    case 0x00137F:
                        map.addWall(x, y, "textures/walls/walls.png", 1);
                        break;
                    case 0xA0A0A0:
                        //map.addDoor(x, y, "textures/doors/door1.png");
                        break;
                    case 0x00FF90:
                        map.addObject(x, y, "textures/objects/objects.png", 16, false, "Lamp");
                        break;
                    case 0x7F6A00:
                        map.addObject(x, y, "textures/objects/objects.png", 21, false, "Bones 1");
                        break;
                    case 0xFFFFFF:
                        map.addObject(x, y, "textures/objects/objects.png", 11, false, "Skeleton");
                        break;
                    case 0x007F7F:
                        map.addAmmo(x, y, "textures/objects/objects.png", 28, 10, "Small Ammo");
                        break;
                    case 0xFF00DC:
                        map.setPlayerSpawn(x, y);
                        break;
                    case 0x00FF21:
                        map.addMedkit(x, y, "textures/objects/objects.png", 26, 10, "Food");
                        break;
                    case 0x00FFFF:
                        map.addExit(x, y);
                        break;
                    case 0x4CFF00:
                        map.addMedkit(x, y, "textures/objects/objects.png", 27, 30, "Medkit");
                        break;
                    case 0xFFD800:
                        map.addTreasure(x, y, "textures/objects/objects.png", 47, 100, "Chalice");
                        break;
                    case 0xFF0000:
                        map.addGuard(x, y);
                        break;
                    case 0xFF6A00:
                        map.addDog(x, y);
                        break;
                }
            }
        }

        return map;
    }
}
