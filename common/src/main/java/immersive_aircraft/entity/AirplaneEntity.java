package immersive_aircraft.entity;

import immersive_aircraft.item.upgrade.VehicleStat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

/**
 * Implements airplane like physics properties and accelerated towards
 */
public abstract class AirplaneEntity extends AircraftEntity {
    public AirplaneEntity(EntityType<? extends AircraftEntity> entityType, Level world, boolean canExplodeOnCrash) {
        super(entityType, world, canExplodeOnCrash);
    }

    @Override
    protected boolean useAirplaneControls() {
        return true;
    }

    @Override
    protected float getGravity() {
        Vector3f direction = getForwardDirection();
        float speed = (float) getDeltaMovement().length() * (0.7f - Math.abs(direction.y));
        return Math.max(0.0f, 4.0f - speed * 12f) * super.getGravity();
    }

    protected float getBrakeFactor() {
        return 0.99f;
    }

    @Override
    protected void updateController() {
        if (!isVehicle()) {
            return;
        }

        super.updateController();

        // engine control
        if (movementY != 0) {
            setEngineTarget(Math.max(0.0f, Math.min(1.0f, getEngineTarget() + 0.05f * movementY)));
            if (movementY < 0) {
                setDeltaMovement(getDeltaMovement().scale(getBrakeFactor()));
            }
        }

        // get direction
        Vector3f direction = getForwardDirection();

        // speed
        float thrust = (float) (Math.pow(getEnginePower(), 5.0) * getProperties().get(VehicleStat.ENGINE_SPEED));
        if (onGround() && getEngineTarget() < 0.9) {
            thrust = getProperties().get(VehicleStat.PUSH_SPEED) / (1.0f + (float) getDeltaMovement().length() * 3.0f) * pressingInterpolatedZ.getSmooth() * (1.0f - getEnginePower());
        }

        // accelerate
        setDeltaMovement(getDeltaMovement().add(toVec3d(direction.mul(thrust))));
    }
}
