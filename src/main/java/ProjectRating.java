enum ProjectRating {
  Zero, One, Two, Three, Four, Five;

  @Override
  public String toString() {
    switch (this) {
      case Zero:
        return "☆☆☆☆☆";
      case One:
        return "★☆☆☆☆";
      case Two:
        return "★★☆☆☆";
      case Three:
        return "★★★☆☆";
      case Four:
        return "★★★★☆";
      case Five:
        return "★★★★★";
      default:
        throw new IllegalArgumentException();
    }
  }
}
