package redstonetweaks.gui.setting;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTWindow;
import redstonetweaks.setting.types.ArraySetting;
import redstonetweaks.setting.types.ISetting;

public class ArraySettingWindow<K, E> extends RTWindow {
	
	private static final int WIDTH = 180;
	private static final int HEIGHT = 185;
	
	private final ArraySetting<K, E> setting;
	private final Supplier<E[]> arraySupplier;
	private final Consumer<ISetting> changeListener;
	
	private ArraySettingListWidget<K, E> list;
	
	private boolean canEdit;
	
	public ArraySettingWindow(RTMenuScreen screen, ArraySetting<K, E> setting, Supplier<E[]> arraySupplier, Consumer<ISetting> changeListener) {
		super(screen, new TranslatableText(setting.getName()), (screen.getWidth() - WIDTH) / 2, (screen.getHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
		
		this.setting = setting;
		this.arraySupplier = arraySupplier;
		this.changeListener = changeListener;
		
		this.canEdit = true;
	}
	
	@Override
	protected void tickContents() {
		list.tick();
	}
	
	@Override
	protected void initContents() {
		list = new ArraySettingListWidget<>(screen, getX(), getY() + getHeaderHeight(), getWidth(), getHeight() - getHeaderHeight(), setting, arraySupplier.get(), changeListener);
		list.init();
		if (!canEdit) {
			list.disableButtons();
		}
		addContent(list);
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		list.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	protected void onRefresh() {
		list.saveScrollAmount();
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return list.focusedIsTextField();
	}
	
	public void disableButtons() {
		canEdit = false;
		refresh();
	}
	
	public void enableButtons() {
		canEdit = true;
		refresh();
	}
}
