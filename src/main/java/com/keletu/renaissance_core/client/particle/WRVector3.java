package com.keletu.renaissance_core.client.particle;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;

public class WRVector3
{
    public float x;
    public float y;
    public float z;
    
    public WRVector3(final double x, final double y, final double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }
    
    public WRVector3(final TileEntity tile) {
        this.x = tile.getPos().getX() + 0.5f;
        this.y = tile.getPos().getY() + 0.5f;
        this.z = tile.getPos().getZ() + 0.5f;
    }
    
    public WRVector3(final Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }
    
    public WRVector3 add(final WRVector3 vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }
    
    public WRVector3 sub(final WRVector3 vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }
    
    public WRVector3 scale(final float scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        return this;
    }
    
    public WRVector3 scale(final float scalex, final float scaley, final float scalez) {
        this.x *= scalex;
        this.y *= scaley;
        this.z *= scalez;
        return this;
    }
    
    public WRVector3 normalize() {
        final float length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }
    
    public float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public float lengthPow2() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public WRVector3 copy() {
        return new WRVector3(this.x, this.y, this.z);
    }
    
    public static WRVector3 crossProduct(final WRVector3 vec1, final WRVector3 vec2) {
        return new WRVector3(vec1.y * vec2.z - vec1.z * vec2.y, vec1.z * vec2.x - vec1.x * vec2.z, vec1.x * vec2.y - vec1.y * vec2.x);
    }
    
    public static WRVector3 xCrossProduct(final WRVector3 vec) {
        return new WRVector3(0.0, vec.z, -vec.y);
    }
    
    public static WRVector3 zCrossProduct(final WRVector3 vec) {
        return new WRVector3(-vec.y, vec.x, 0.0);
    }
    
    public static float dotProduct(final WRVector3 vec1, final WRVector3 vec2) {
        return vec1.x * vec2.x + vec1.y * vec2.y + vec1.z * vec2.z;
    }
    
    public static float angle(final WRVector3 vec1, final WRVector3 vec2) {
        return anglePreNorm(vec1.copy().normalize(), vec2.copy().normalize());
    }
    
    public static float anglePreNorm(final WRVector3 vec1, final WRVector3 vec2) {
        return (float)Math.acos(dotProduct(vec1, vec2));
    }

    public WRVector3 rotate(final float angle, final WRVector3 axis) {
        return WRMat4.rotationMat(angle, axis).translate(this);
    }

    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "," + this.z + "]";
    }
    
    public Vec3d toVec3D() {
        return new Vec3d(this.x, this.y, this.z);
    }
    
    public static WRVector3 getPerpendicular(final WRVector3 vec) {
        if (vec.z == 0.0f) {
            return zCrossProduct(vec);
        }
        return xCrossProduct(vec);
    }
    
    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f;
    }
}
