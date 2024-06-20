package net.bigmangohead.crystalworks.block.entity.abstraction;

import net.bigmangohead.crystalworks.util.energy.flux.FluxStorage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public interface ISmallAttachableToCrystal {

    <T extends IEnergyStorage> LazyOptional<T> getEnergyOptional();

    LazyOptional<FluxStorage> getFluxOptional();

}
