package net.omega2097.map;

import net.omega2097.util.IRandom;

import java.util.ArrayList;
import java.util.List;

public class RandomRoomGenerator<T extends IMap> {
    private int width;
    private int height;
    private int maxRooms;
    private int roomMinSize;
    private int roomMaxSize;
    private IRandom random;

    public RandomRoomGenerator(int width, int height, int maxRooms, int roomMinSize, int roomMaxSize, IRandom random) {
        this.width = width;
        this.height = height;
        this.maxRooms = maxRooms;
        this.roomMinSize = roomMinSize;
        this.roomMaxSize = roomMaxSize;
        this.random = random;
    }

    public T createMap(T map) {
        List<Rectangle> rooms = new ArrayList<>();
        map.init(width, height);
        for (int r = 0; r < maxRooms; r++) {
            int roomWidth = random.next(roomMinSize, roomMaxSize);
            int roomHeight = random.next(roomMinSize, roomMaxSize);
            int xPos = random.next(0, width - roomWidth - 1);
            int yPos = random.next(0, height - roomHeight - 1);

            Rectangle newRoom = new Rectangle(xPos, yPos, roomWidth, roomHeight);
            boolean newRoomIntersects = false;
            for(Rectangle room : rooms) {
                if (newRoom.intersects(room)) {
                    newRoomIntersects = true;
                    break;
                }
            }
            if (!newRoomIntersects) {
                rooms.add(newRoom);
            }
        }

        for(Rectangle room : rooms) {
            makeRoom(map, room);
        }

        for (int r = 0; r < rooms.size(); r++) {
            if (r == 0) {
                continue;
            }
            int prevRoomCenterX = rooms.get(r-1).getCenterX();
            int prevRoomCenterY = rooms.get(r-1).getCenterY();
            int currentRoomCenterX = rooms.get(r).getCenterX();
            int currentRoomCenterY = rooms.get(r).getCenterY();

            if ( random.next( 0, 2 ) == 0 )
            {
                makeHorizontalTunnel( map, prevRoomCenterX, currentRoomCenterX, prevRoomCenterY );
                makeVerticalTunnel( map, prevRoomCenterY, currentRoomCenterY, currentRoomCenterX );
            }
            else
            {
                makeVerticalTunnel( map, prevRoomCenterY, currentRoomCenterY, prevRoomCenterX );
                makeHorizontalTunnel( map, prevRoomCenterX, currentRoomCenterX, currentRoomCenterY );
            }
        }

        return map;
    }
    public void makeRoom(T map, Rectangle room) {
        for (int x = room.getX() + 1; x < room.getRight(); x++) {
            for (int y = room.getY() + 1; y < room.getBottom(); y++) {
                map.setProperties(x, y, true, true);
            }
        }
    }
    public void makeHorizontalTunnel(T map, int startX, int endX, int yPos) {
        for ( int x = Math.min( startX, endX ); x <= Math.max( startX, endX ); x++ ) {
            map.setProperties(x, yPos, true, true);
        }
    }
    private void makeVerticalTunnel(T map, int startY, int endY, int xPos )
    {
        for ( int y = Math.min( startY, endY ); y <= Math.max( startY, endY ); y++ )
        {
            map.setProperties(xPos, y, true, true);
        }
    }
}
