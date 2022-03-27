package frozenblock.wild.mod.liukrastapi;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

/**
 * Base GUI interface for handling callbacks related to
 * keyboard or mouse actions.
 * 
 * Mouse coordinate is bounded by the size of the window in
 * pixels.
 */
@Environment(EnvType.CLIENT)
public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {
	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final float length;
		private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
		private boolean looping;

		public static AnimationDefinition.Builder withLength(float f) {
			return new AnimationDefinition.Builder(f);
		}

		private Builder(float f) {
			this.length = f;
		}

		public AnimationDefinition.Builder looping() {
			this.looping = true;
			return this;
		}

		public AnimationDefinition.Builder addAnimation(String string, AnimationChannel animationChannel) {
			this.animationByBone.computeIfAbsent(string, stringx -> Lists.newArrayList()).add(animationChannel);
			return this;
		}

		public AnimationDefinition build() {
			return new AnimationDefinition(this.length, this.looping, this.animationByBone);
		}
	}
}
