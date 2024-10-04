package org.example

sealed class Figure(open val property: Double) {
    data class Circle(override val property: Double) : Figure(property) {
        init {
            println("Circle(property=$property)")
        }
    }

    data class Square(override val property: Double) : Figure(property) {
        init {
            println("Square(property=$property)")
        }
    }
}

interface ConsoleService {
    fun work()
}

interface FigureService {
    fun addSquare(property: Double)
    fun addCircle(property: Double)
    fun getPerimeter(): Double
    fun getArea(): Double
}

class FigureServiceImpl : FigureService {
    private val figures = mutableListOf<Figure>()

    override fun addSquare(property: Double) {
        if (property <= 0 || property.isNaN()) throw BadPropertyException(property)
        figures.add(Figure.Square(property))
    }

    override fun addCircle(property: Double) {
        if (property <= 0 || property.isNaN()) throw BadPropertyException(property)
        figures.add(Figure.Circle(property))
    }

    override fun getPerimeter(): Double {
        return figures.sumOf {
            when (it) {
                is Figure.Circle -> 2 * Math.PI * it.property
                is Figure.Square -> 4 * it.property
            }
        }
    }

    override fun getArea(): Double {
        return figures.sumOf {
            when (it) {
                is Figure.Circle -> Math.PI * it.property * it.property
                is Figure.Square -> it.property * it.property
            }
        }
    }
}

class BadPropertyException(val property: Double) : Exception("Bad property value: $property")
class WrongOperationTypeException : Exception("Wrong operation type")

class ConsoleServiceImpl(private val figureService: FigureService) : ConsoleService {
    override fun work() {
        while (true) {
            println(
                "Введите тип операции, которую хотите исполнить:\n" +
                        "1) добавить фигуру\n" +
                        "2) получить площадь всех фигур\n" +
                        "3) получить периметр всех фигур\n" +
                        "4) завершить выполнение"
            )

            val operationInput = readLine()
            try {
                val operation = getOperation(operationInput)
                when (operation) {
                    Operation.INSERT -> addFigure()
                    Operation.GET_AREA -> getArea()
                    Operation.GET_PERIMETER -> getPerimeter()
                    Operation.EXIT -> break
                }
            } catch (e: BadPropertyException) {
                println("Введено неверное значение параметра property: ${e.property}")
            } catch (e: WrongOperationTypeException) {
                println("Введен неизвестный тип операции: ${operationInput}")
            }
        }
    }

    private fun addFigure() {
        println("Введите тип фигуры (1 - Square, 2 - Circle):")
        val figureType = readLine()
        println("Введите значение property:")
        val property = readLine()?.toDoubleOrNull()

        if (property == null) {
            println("Введено неверное значение property.")
            return
        }

        when (figureType) {
            "1" -> figureService.addSquare(property)
            "2" -> figureService.addCircle(property)
            else -> println("Неверный тип фигуры.")
        }
    }

    private fun getArea() {
        println("Общая площадь всех фигур: ${figureService.getArea()}")
    }

    private fun getPerimeter() {
        println("Общий периметр всех фигур: ${figureService.getPerimeter()}")
    }
}

enum class Operation {
    INSERT, GET_AREA, GET_PERIMETER, EXIT
}

fun getOperation(input: String?): Operation {
    return when (input) {
        "1" -> Operation.INSERT
        "2" -> Operation.GET_AREA
        "3" -> Operation.GET_PERIMETER
        "4" -> Operation.EXIT
        else -> throw WrongOperationTypeException()
    }
}

fun main() {
    val figureService = FigureServiceImpl()
    val consoleService = ConsoleServiceImpl(figureService)
    consoleService.work()
}