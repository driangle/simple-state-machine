package ggr.sm

// import java.util.concurrent.atomic.AtomicBoolean

case class StateMachine[State, Input](initialState : State, transition : (State, Input) => State) {

  private var currentState : State = initialState

  def getCurrentState : State = currentState

  def consume(input : Input) : State = {
    this.currentState = transition.apply(this.currentState, input)
    this.currentState
  }

  def reset() : State  = {
    this.currentState = initialState
    this.currentState
  }

  def set(newState : State) = {
    this.currentState = newState
  }

}

object StateMachine {

  case class Builder[State, Input]() {

    case class Transition(next : PartialFunction[(State, Input), State])

    private var initialState : Option[State] = None
    private var transitions : Seq[Transition] = List.empty

    def withInitialState(state : State) : StateMachine.Builder[State, Input] = {
      initialState = Some(state)
      this
    }
    def transition(next : PartialFunction[(State, Input), State]) : StateMachine.Builder[State, Input] = {
      transitions = transitions :+ Transition(next)
      this
    }

    def build() : StateMachine[State, Input] = {
      if (initialState.isEmpty) {
        throw new IllegalStateException("You must set the initial state of the state machine using 'withInitialState'")
      }
      StateMachine(initialState.get, (state, input) => {
        transitions.filter(_.next.isDefinedAt(state, input)).headOption
          .map(_.next.apply(state, input)) match {
          case Some(next) => next
          case None => state // no transition matched input, stay in current state
        }
      })
    }
  }
}
