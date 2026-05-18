package pRed

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage


//question: is andR implementation is parallel? NO
class ANDR extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(Bool())
  })
  io.out := io.in.andR
}

//question: is PriorityMux implementation is parallel? NO
class PRIORITYMUX extends Module {
  val io = IO(new Bundle {
    val inContrl = Input(UInt(8.W))
    val inData   = Input(Vec(8, UInt(32.W)))
    val out      = Output(UInt(32.W))
  })
  io.out := PriorityMux(io.inContrl, io.inData)
}

object Main extends App {
  ChiselStage.emitSystemVerilogFile(
    new PRIORITYMUX,
    firtoolOpts = Array(
      "-disable-all-randomization", 
      "-strip-debug-info", 
      "-default-layer-specialization=enable")
  )
}
