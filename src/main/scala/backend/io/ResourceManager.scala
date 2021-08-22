package backend.io

import backend.engine.{CompositeOperation, OperationManager, Selection, SelectionManager}
import backend.layers.{Image, ImageManager}

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

@SerialVersionUID(112L)
case class ResourceManager() extends Serializable {
  val imageManager: List[Image] = ImageManager.imageBuffer.toList
  val operationManager: List[CompositeOperation] = OperationManager.composites.toList
  val selectionManager: List[Selection] = SelectionManager.buffer.toList

  def write(name: String): Unit = {
    ImageManager.overwrite()
    val oos = new ObjectOutputStream(new FileOutputStream(name))
    oos.writeObject(this)
    oos.close()
  }

  def read(name: String): Unit = {
    val ois = new ObjectInputStream(new FileInputStream(name))
    val readManagers = ois.readObject().asInstanceOf[ResourceManager]

    ImageManager.imageBuffer.clear()
    ImageManager.imageBuffer.addAll(readManagers.imageManager)

    OperationManager.composites.clear()
    OperationManager.composites.addAll(readManagers.operationManager)

    SelectionManager.buffer.clear()
    SelectionManager.buffer.addAll(readManagers.selectionManager)
    ois.close()
  }
}
