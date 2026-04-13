package controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

import model.IGameModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.IGameView;

class GraphicsControllerTest {

  private IGameModel mockModel;
  private IGameView mockView;
  private GraphicsController controller;

  @BeforeEach
  void setUp() {
    mockModel = mock(IGameModel.class);
    mockView = mock(IGameView.class);
    controller = new GraphicsController(mockModel, mockView);
  }

  @Test
  void onMoveCallsModelMove() {
    when(mockModel.move("N")).thenReturn("You moved north.");
    controller.onMove("N");
    verify(mockModel).move("N");
  }

  @Test
  void onSaveCallsModelSave() {
    when(mockModel.save()).thenReturn("Game saved.");
    controller.onSave();
    verify(mockModel).save();
  }

  @Test
  void onRestoreCallsModelRestore() {
    when(mockModel.restore()).thenReturn("Game restored.");
    controller.onRestore();
    verify(mockModel).restore();
  }

  @Test
  void onDropCallsModelDrop() {
    when(mockView.getSelectedInventoryItem()).thenReturn("Lamp");
    when(mockModel.dropItem("Lamp")).thenReturn("You dropped Lamp.");
    controller.onDrop();
    verify(mockModel).dropItem("Lamp");
  }

  @Test
  void onUseCallsModelUse() {
    when(mockView.getSelectedInventoryItem()).thenReturn("Lamp");
    when(mockModel.useItem("Lamp")).thenReturn("You used Lamp.");
    controller.onUse();
    verify(mockModel).useItem("Lamp");
  }

  @Test
  void onExitDisposesView() {
    controller.onExit();
    verify(mockView).dispose();
  }
}
