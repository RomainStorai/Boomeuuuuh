package fr.imt.boomeuuuuh.players;

import com.google.common.primitives.Ints;

public class Location {

    final int x, y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public byte[] toByteArray() {
        byte[] array = new byte[2];
        array[0] = (byte) x;
        array[1] = (byte) y;
        return array;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (x != location.x) return false;
        return y == location.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public static Location fromBytesArray(byte[] array) {
        return new Location(array[0], array[1]);
    }
}
