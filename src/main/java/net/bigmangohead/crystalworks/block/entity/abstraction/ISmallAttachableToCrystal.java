package net.bigmangohead.crystalworks.block.entity.abstraction;

import net.bigmangohead.crystalworks.util.energy.flux.IFluxStorage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public interface ISmallAttachableToCrystal {

    LazyOptional<IEnergyStorage> getEnergyOptional();

    LazyOptional<IFluxStorage> getFluxOptional();

}
