package net.frozenblock.wildmod.entity.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelTransform;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ModelPartData {
    private final List<ModelCuboidData> cuboidData;
    private final ModelTransform rotationData;
    private final Map<String, ModelPartData> children = Maps.newHashMap();

    ModelPartData(List<ModelCuboidData> cuboidData, ModelTransform rotationData) {
        this.cuboidData = cuboidData;
        this.rotationData = rotationData;
    }

    public ModelPartData addChild(String name, ModelPartBuilder builder, ModelTransform rotationData) {
        ModelPartData modelPartData = new ModelPartData(builder.build(), rotationData);
        ModelPartData modelPartData2 = this.children.put(name, modelPartData);
        if (modelPartData2 != null) {
            modelPartData.children.putAll(modelPartData2.children);
        }

        return modelPartData;
    }

    public ModelPart createPart(int textureWidth, int textureHeight) {
        Object2ObjectArrayMap<String, ModelPart> object2ObjectArrayMap = this.children
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().createPart(textureWidth, textureHeight),
                                (modelPartx, modelPart2) -> modelPartx,
                                Object2ObjectArrayMap::new
                        )
                );
        List<ModelPart.Cuboid> list = this.cuboidData
                .stream()
                .map(modelCuboidData -> modelCuboidData.createCuboid(textureWidth, textureHeight))
                .collect(ImmutableList.toImmutableList());
        ModelPart modelPart = new ModelPart(list, object2ObjectArrayMap);
        modelPart.setDefaultTransform(this.rotationData);
        modelPart.setTransform(this.rotationData);
        return modelPart;
    }

    public ModelPartData getChild(String name) {
        return (ModelPartData)this.children.get(name);
    }
}
