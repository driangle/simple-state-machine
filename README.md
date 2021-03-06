# simple-state-machine : A simple/lightweight state machine in Scala
`version: 0.1.0`

This package contains a simple/lightweight library for implementing state machines in Scala.
The library allows the client to use their own state, input types and transition functions.

My main goals are:
 - to provide a library that is easy to use, it has a simple API.
 - to provide a library with zero dependencies.

Currently only one implementation of the StateMachine interface exists. I am open to suggestions for other types of state machines:
- `StateMachine.WithFunctionTransitions[State, Input]`: allows clients to specify transitions of type `PartialFunction[(State, Input), State]`. 
   If the transition is not defined for a (State, Input) then the StateMachine will remain in its current state.

## Usage


#### Add a dependency on this package
```scala
  resolvers += Resolver.sonatypeRepo("public")
  ...  
  libraryDependencies += "org.driangle" %% "simple-state-machine" % "0.1.0"
```

#### Define a StateMachine

```scala
import org.driangle.sm.StateMachine

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
```
#### Use a StateMachine

```scala

// currentState: gets the current state of the StateMachine
val state = sm.currentState 

// consume(input) : causes the StateMachine to consume an input and advance to the next state according to its transition function
val nextState = sm.consume("Begin") 

// peek(input): allows a client to peek at the next state if the StateMachine were to consume the given input.
val potentialNextState = sm.peek("Pause")

// reset() : allows client to reset StateMachine back to its initial state
val initialState = sm.reset()

// set(state) : allows client to forcefully change the state of teh StateMachine
sm.set("CustomState")
```

### More advanced example
```scala
import org.driangle.sm.StateMachine

case class UIState(clicks : Seq[Point])

sealed trait UserEvent
case class MousePressed(mouse : Point) extends UserEvent
case class KeyPressed(key : Char) extends UserEvent

val sm = StateMachine.WithFunctionTransitions[UIState, UserEvent]()
      .initialState(UIState())
      .transition({
        // when Mouse is pressed, then add location to list of clicks
        case (state, MousePressed(mouse)) => state.copy(clicks = state.clicks :+ mouse)
        // when key 'c' is pressed then clear the mouse clicks
        case (state, KeyPressed(key)) if key == 'c' => state.copy(clicks = List.empty)
      })
      .build()
```

## License
```
   This software is licensed under the Apache 2 license, quoted below.

   Copyright 2019 Germán Greiner

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
