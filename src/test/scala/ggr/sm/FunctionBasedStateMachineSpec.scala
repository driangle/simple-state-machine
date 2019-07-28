package ggr.sm

import org.scalatest.FunSuite

class FunctionBasedStateMachineSpec extends FunSuite {

  test("A user should be able to build a simple toggle state machine") {
    val sm = StateMachine.WithFunctionTransitions[String, Unit]()
      .initialState("off")
      .transition({
        case ("off", _) => "on"
        case ("on", _) => "off"
      })
      .build()

    assert(sm.currentState == "off")
    sm.consume()
    assert(sm.currentState == "on")
    sm.consume()
    assert(sm.currentState == "off")
    sm.consume()
    assert(sm.currentState == "on")
  }

  test("A user MUST set the value for the initial state of the state machine") {
    intercept[IllegalStateException] {
      StateMachine.WithFunctionTransitions[String, Unit]()
        .transition({
          case ("off", _) => "on"
          case ("on", _) => "off"
        })
        .build()
    }
  }

  test("A user MUST set at least one transition for the state machine") {
    intercept[IllegalStateException] {
      StateMachine.WithFunctionTransitions[String, Unit]()
        .initialState("off")
        .build()
    }
  }

  test("A user cannot provide a null transition") {
    intercept[IllegalArgumentException] {
      StateMachine.WithFunctionTransitions[String, Unit]()
        .initialState("off")
        .transition(null)
        .build()
    }
  }

  test("A user cannot provide a null transition when using the class constructor") {
    intercept[IllegalArgumentException] {
      new FunctionBasedStateMachine(null, null)
    }
  }

  // https://stackoverflow.com/a/5924053/476260
  test("A user should be able to build a simple state machine with several states and transitions") {
    val sm = StateMachine.WithFunctionTransitions[String, String]()
      .initialState("Inactive")
      .transition({
        case ("Inactive", "Begin") => "Active"
        case ("Inactive", "Exit") => "Exit"
        case ("Active", "End") => "Inactive"
        case ("Active", "Pause") => "Paused"
        case ("Paused", "Resume") => "Active"
        case ("Paused", "End") => "Inactive"
      })
      .build()

    assert(sm.currentState == "Inactive")
    sm.consume("Begin")
    assert(sm.currentState == "Active")
    sm.consume("Pause")
    assert(sm.currentState == "Paused")
    sm.consume("Resume")
    assert(sm.currentState == "Active")
    sm.consume("Pause")
    assert(sm.currentState == "Paused")
    sm.consume("End")
    assert(sm.currentState == "Inactive")
    sm.consume("Begin")
    assert(sm.currentState == "Active")
    sm.consume("End")
    assert(sm.currentState == "Inactive")
    sm.consume("Exit")
    assert(sm.currentState == "Exit")
  }

  test("A user should be able to peek at the next state of StateMachine without actually changing it") {
    val sm = StateMachine.WithFunctionTransitions[String, String]()
      .initialState("Inactive")
      .transition({
        case ("Inactive", "Begin") => "Active"
        case ("Inactive", "Exit") => "Exit"
        case ("Active", "End") => "Inactive"
        case ("Active", "Pause") => "Paused"
        case ("Paused", "Resume") => "Active"
        case ("Paused", "End") => "Inactive"
      })
      .build()

    assert(sm.currentState == "Inactive")
    assert(sm.peek("Begin") == "Active")
    assert(sm.currentState == "Inactive")
    assert(sm.consume("Begin") == "Active")
    assert(sm.currentState == "Active")
  }

  test("A user should be able to reset a state machine back to its original state") {
    val sm = StateMachine.WithFunctionTransitions[String, String]()
      .initialState("Inactive")
      .transition({
        case ("Inactive", "Begin") => "Active"
        case ("Inactive", "Exit") => "Exit"
        case ("Active", "End") => "Inactive"
        case ("Active", "Pause") => "Paused"
        case ("Paused", "Resume") => "Active"
        case ("Paused", "End") => "Inactive"
      })
      .build()

    assert(sm.currentState == "Inactive")
    sm.consume("Exit")
    assert(sm.currentState == "Exit")
    sm.reset()
    assert(sm.currentState == "Inactive")
  }


  test("A user should be able to forcefully set the state of the state machine") {
    val sm = StateMachine.WithFunctionTransitions[String, String]()
      .initialState("Inactive")
      .transition({
        case ("Inactive", "Begin") => "Active"
        case ("Inactive", "Exit") => "Exit"
        case ("Active", "End") => "Inactive"
        case ("Active", "Pause") => "Paused"
        case ("Paused", "Resume") => "Active"
        case ("Paused", "End") => "Inactive"
      })
      .build()

    assert(sm.currentState == "Inactive")
    sm.set("Active")
    assert(sm.currentState == "Active")
    sm.set("Other")
    assert(sm.currentState == "Other")
  }
}