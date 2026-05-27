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
object ParallelAND {
  def apply[T <: Data](xs: Seq[T]): T = {
    ParallelOperation(xs, (a: T, b:T) => (a.asUInt & b.asUInt).asTypeOf(xs.head))
  }
}

//question: is PriorityMux implementation is parallel? NO
class Check1PriorityMux extends Module {
  val io = IO(new Bundle {
    val inContrl = Input(UInt(8.W))
    val inData   = Input(Vec(8, UInt(32.W)))
    val out      = Output(UInt(32.W))
  })
  io.out := PriorityMux(io.inContrl, io.inData)
}


object ParallelOperation {
  def apply[T](xs: Seq[T], func: (T, T) => T): T = {
    require(xs.nonEmpty)
    xs match {
      case Seq(a) => a
      case Seq(a, b) => func(a, b)
      case _ =>
        apply(Seq(apply(xs take xs.size/2, func), apply(xs drop xs.size/2, func)), func)
    }
  }
}

object ParallelPriorityMux {
  def apply[T <: Data](in: Seq[(Bool, T)]): T = {
    ParallelOperation(in, (a: (Bool, T), b: (Bool, T)) => (a._1 || b._1, Mux(a._1, a._2, b._2)))._2
  }
  def apply[T <: Data](sel: Bits, in: Seq[T]): T = apply((0 until in.size).map(sel(_)), in)
  def apply[T <: Data](sel: Seq[Bool], in: Seq[T]): T = apply(sel zip in)
}

object PriorityMux {
  def apply[T <: Data](in: Seq[(Bool, T)]): T = {ParallelPriorityMux(in)}
  def apply[T <: Data](sel: Bits, in: Seq[T]): T = apply((0 until in.size).map(sel(_)), in)
  def apply[T <: Data](sel: Seq[Bool], in: Seq[T]): T = apply(sel zip in)
}

//make a parallel PriorityEncoder
object ParallelPriorityEncoder {
  def apply(in: Seq[Bool]): UInt = ParallelPriorityMux(in, (0 until in.size).map(_.asUInt))
  def apply(in: Bits): UInt = apply(in.asBools)
}
object PriorityEncoder {
  def apply(in: Bits): UInt = ParallelPriorityEncoder(in)
}
class Check1PriorityEncoder extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(3.W))
  })
  io.out := PriorityEncoder(io.in)
}

object ParallelPriorityEncoderOH {
  def apply(in: Seq[Bool]): UInt = ParallelPriorityMux(in, (0 until in.size).map(i => UIntToOH(i.asUInt)))
  def apply(in: Bits): UInt = apply(in.asBools)
}
object PriorityEncoderOH {
  def apply(in: Bits): UInt = ParallelPriorityEncoderOH(in)
}
class Check1PriorityEncoderOH extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })
  io.out := PriorityEncoderOH(io.in)
}

object Main extends App {
  ChiselStage.emitSystemVerilogFile(
    new Check1PriorityEncoderOH,
    firtoolOpts = Array(
      "-disable-all-randomization", 
      "-strip-debug-info", 
      "-default-layer-specialization=enable")
  )
}
