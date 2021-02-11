package com.loucaskreger.entityaccessors.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class SerializableEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {
	public static final String ENERGY_KEY = "energy";

	public SerializableEnergyStorage(int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void addEnergy(int energy) {
		this.energy += energy;
		if (this.energy > this.getMaxEnergyStored()) {
			this.energy = this.getMaxEnergyStored();
		}
	}

	public void consumeEnergy(int energy) {
		this.energy -= energy;
		if (this.energy < 0) {
			this.energy = 0;
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt(ENERGY_KEY, this.getEnergyStored());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.energy = nbt.getInt(ENERGY_KEY);
	}

}
