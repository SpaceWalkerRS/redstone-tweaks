package redstone.tweaks.g4mespeed;

import com.g4mesoft.GSExtensionInfo;
import com.g4mesoft.GSExtensionUID;
import com.g4mesoft.GSIExtension;
import com.g4mesoft.core.GSVersion;
import com.g4mesoft.core.client.GSClientController;
import com.g4mesoft.core.server.GSServerController;
import com.g4mesoft.packet.GSIPacket;
import com.g4mesoft.registry.GSSupplierRegistry;
import com.g4mesoft.setting.GSSettingManager;

import redstone.tweaks.RedstoneTweaksMod;
import redstone.tweaks.g4mespeed.setting.decoder.CapacitorBehaviorSettingDecoder;
import redstone.tweaks.g4mespeed.setting.decoder.QuasiConnectivitySettingDecoder;
import redstone.tweaks.g4mespeed.setting.decoder.TickPrioritySettingDecoder;

public class RedstoneTweaksExtension implements GSIExtension {

	public static final String NAME = RedstoneTweaksMod.MOD_NAME;
	public static final GSExtensionUID UID = new GSExtensionUID(0x5253544D); /* RSTM in hex */
	public static final GSVersion VERSION = new GSVersion(RedstoneTweaksMod.MOD_VERSION);

	public static final GSExtensionInfo INFO = new GSExtensionInfo(NAME, UID, VERSION);

	private final RedstoneTweaksModule clientModule;
	private final RedstoneTweaksModule serverModule;

	public RedstoneTweaksExtension() {
		this.clientModule = new RedstoneTweaksModule();
		this.serverModule = new RedstoneTweaksModule();
	}

	@Override
	public void addClientModules(GSClientController controller) {
		controller.addModule(clientModule);
	}

	@Override
	public void addServerModules(GSServerController controller) {
		controller.addModule(serverModule);
	}

	@Override
	public GSExtensionInfo getInfo() {
		return INFO;
	}

	@Override
	public String getTranslationPath() {
		return "";
	}

	@Override
	public void init() {
		GSSettingManager.registerDecoder(new CapacitorBehaviorSettingDecoder());
		GSSettingManager.registerDecoder(new QuasiConnectivitySettingDecoder());
		GSSettingManager.registerDecoder(new TickPrioritySettingDecoder());
	}

	@Override
	public void registerPackets(GSSupplierRegistry<Integer, GSIPacket> registry) {

	}
}
