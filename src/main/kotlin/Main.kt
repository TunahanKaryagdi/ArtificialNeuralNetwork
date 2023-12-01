import kotlin.math.exp
import kotlin.math.pow
import kotlin.random.Random

fun main(args: Array<String>) {

    val dataTable = listOf(
        listOf(0.0,0.0,0.0,1.0),
        listOf(0.0,0.0,1.0,0.0),
        listOf(0.0,1.0,0.0,0.0),
        listOf(1.0,0.0,0.0,0.0),
    )

    val expectedDataTable = listOf(
        listOf(0.0,0.0),
        listOf(0.0,1.0),
        listOf(1.0,0.0),
        listOf(1.0,1.0),
    )

    init(data = dataTable[0], expectedData = expectedDataTable[0])
    repeat(repeatCount){index->
        for (i in 0 until 4){
            println("--------- $i. number data row--------------")
            setNewValues(data = dataTable[i], expectedData = expectedDataTable[i])
            forward()
            backward()
        }
    }

}

const val repeatCount = 5
const val midCount = 5
const val outputCount = 2
const val learningCoefficient = 0.1



data class Input(var value: Double,var weights: MutableList<Double>)
data class Mid(var value: Double, val weights: MutableList<Double>, var error: Double)
data class Output(var value: Double,var error: Double)


var inputs = mutableListOf<Input>()
var mids = mutableListOf<Mid>()
var outputs = mutableListOf<Output>()
var expectedOutputs = mutableListOf<Double>()


fun init(data:List<Double>,expectedData: List<Double>){

    data.forEach { value->
        inputs.add(Input(value = value, weights = MutableList(midCount){Random.nextDouble()}))
    }
    expectedData.forEach { value->
        expectedOutputs.add(value)
    }


    for (i in 0 until midCount){
        mids.add(
            Mid(
                value = 0.0,
                error = 0.0,
                weights =  MutableList(outputCount){ Random.nextDouble()}
            )
        )
    }

    for(i in 0 until outputCount){
        outputs.add(
            Output(
                value = 0.0,
                error = 0.0
            )
        )
    }
}


fun setNewValues(data:List<Double>,expectedData: List<Double>){
    inputs.forEachIndexed { index, input ->
        input.value = data[index]
    }

    expectedOutputs.forEachIndexed {index, d ->
        expectedOutputs[index] = expectedData[index]
    }
}

fun forward(){

    mids.mapIndexed {index, mid ->
        mid.value = calculateMidValue(index)
    }

    outputs.mapIndexed { index, output ->
        output.value = calculateOutputValue(index)
    }
    printWeights()


}

fun backward(){
    calculateOutputError()
    calculateMidError()
    setNewInputWeights()
    setNewMidWeights()
}



fun calculateMidValue(index: Int) : Double{
    var sum  = 0.0
    inputs.forEach {
        sum += it.value * it.weights[index]
    }

    return activationFun(sum)
}

fun calculateOutputValue(index: Int) : Double{
    var sum  = 0.0
    mids.forEach {
        sum += it.value * it.weights[index]
    }
    return activationFun(sum)
}

fun calculateOutputError(){

    outputs.mapIndexed { index, output ->
        output.error = output.value * (1- output.value) * (expectedOutputs[index] - output.value)
    }

}

fun calculateMidError(){

    var sigma = 0.0
    mids.forEachIndexed { index, mid ->
        mid.weights.forEachIndexed{ index, d ->  
            sigma += d * outputs[index].error
        }
        mid.error = mid.value * (1- mid.value) * sigma
        sigma = 0.0
    }

}

fun setNewInputWeights(){

    inputs.forEachIndexed { ind, input ->
        input.weights.mapIndexed { index, d ->
            input.weights[index] = d + (learningCoefficient * input.value * mids[index].error)
        }
    }
}

fun setNewMidWeights(){
    mids.forEachIndexed { ind, mid ->
        mid.weights.mapIndexed { index, d ->
            mid.weights[index] = d + (learningCoefficient * mid.value * outputs[index].error)
        }
    }
}
fun calculateTotalError(): Double{
    var sum =0.0
    outputs.forEachIndexed { index, output ->
        sum += (expectedOutputs[index] - output.value).pow(2)
    }
    return sum/2
}

fun activationFun(value : Double) : Double{
    return 1/ (1+ exp(-value))
}

fun printWeights(){
    inputs.forEachIndexed {index, input ->  println("$index number input value: ${input.value} weights: ${input.weights}") }
    mids.forEachIndexed {index, input ->  println("$index number mid value ${input.value} weights ${input.weights}") }
    printOutputs()
    println("Total Error: ${calculateTotalError()}")
    println("--------------------------------------------------------------------------")

}

fun printOutputs(){
    outputs.forEachIndexed {index, output ->  println("$index number output ${output.value}") }
}


