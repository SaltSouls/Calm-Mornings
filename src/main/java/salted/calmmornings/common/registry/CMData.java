package salted.calmmornings.common.registry;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import salted.calmmornings.CalmMornings;

import java.util.function.Supplier;

public class CMData {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CalmMornings.MODID);

    public static final Supplier<AttachmentType<String>> SLEEPTIME = ATTACHMENTS.register(
            "sleeptime", () -> AttachmentType.builder(() -> "awake").serialize(Codec.STRING).build()
    );

}
