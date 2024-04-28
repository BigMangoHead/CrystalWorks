package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.util.energy.flux.FluxType;

// Definitely not how registering stuff is supposed to work,
// but I don't currently understand this well to do this properly
// TODO: Figure out proper registration of flux types so that other mods can implement this and the system is more robust
public class ModFluxTypes {

    public static final FluxType DIAMOND = new FluxType(1, 1, 1, 1);

}
