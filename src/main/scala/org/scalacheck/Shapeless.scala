package org.scalacheck

import shapeless._
import shapeless.ops.coproduct.Length
import shapeless.ops.nat.ToInt

object Shapeless {

  implicit val hnilArbitrary: Arbitrary[HNil] =
    Arbitrary(Gen.const(HNil))
  
  implicit def hconsArbitrary[H, T <: HList](implicit
    headArbitrary: Lazy[Arbitrary[H]],
    tailArbitrary: Lazy[Arbitrary[T]]      
  ): Arbitrary[H :: T] =
    Arbitrary {
      for {
        h <- headArbitrary.value.arbitrary
        t <- tailArbitrary.value.arbitrary
      } yield h :: t
    }
  
  implicit val cnilArbitrary: Arbitrary[CNil] = Arbitrary(Gen.fail)

  implicit def cconsEquallyWeightedArbitrary[H, T <: Coproduct, N <: Nat](implicit
    headArbitrary: Lazy[Arbitrary[H]],
    tailArbitrary: Lazy[Arbitrary[T]],
    length: Length.Aux[T, N],
    n: ToInt[N]
  ): Arbitrary[H :+: T] =
    Arbitrary {
      Gen.frequency(
        1 -> headArbitrary.value.arbitrary.map(Inl(_)),
        n() -> tailArbitrary.value.arbitrary.map(Inr(_))
      )
    }

  implicit def instanceArbitrary[F, G](implicit
    gen: Generic.Aux[F, G],
    arbitrary: Lazy[Arbitrary[G]]
  ): Arbitrary[F] =
    Arbitrary(arbitrary.value.arbitrary.map(gen.from))
  
}
