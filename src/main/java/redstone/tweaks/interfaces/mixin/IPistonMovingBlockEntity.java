package redstone.tweaks.interfaces.mixin;

public interface IPistonMovingBlockEntity {

	void init(PistonOverrides source);

	float getAmountPerStep();

}
