package dev.happyaccidents;

import dev.happyaccidents.modules.BlockTracers;
import dev.happyaccidents.modules.EntityTracers;
import dev.happyaccidents.modules.SignRender;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class HappyAccidents extends MeteorAddon {
    public static final Category CATEGORY = new Category("Happy Accidents");

    @Override
    public void onInitialize() {
        Modules.get().add(new EntityTracers());
        Modules.get().add(new BlockTracers());
        Modules.get().add(new SignRender());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "dev.happyaccidents";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("Ross5521", "happyaccidents");
    }
}
