
/**
   * Gets the miniplayer title to be displayed
   *
   * @returns {string} title to display
   */
export const getTitleFromVideoInfo = ({title, castingTo}) => {
    if (title && title?.length) {
      return title;
    }

    if (castingTo) {
      return `Casting to ${castingTo}`;
    } else {
      return "";
    }
  };

  /**
   * Gets the miniplayer subtitle to be displayed
   *
   * @returns {string} subtitle to display
   */
  export const getSubTitleFromVideoInfo = ({title, subTitle, castingTo}) => {
    if (subTitle && subTitle?.length) {
      return subTitle;
    }

    if (title && title?.length) {
      return `Casting to ${castingTo}`;
    }

    return "";
  };

/**
   * Gets the default color for the apperance
   *
   * @returns {Object} default color object
   */
  export function getApperanceColor(isDark) {

    if (!isDark){
      return {
        viewBackgroundColor: "#FFFFFF",
        progressViewBackgroundColor:"#000000",
        progressBarColor:"red",
        titleTextColor: "#000000",
        subtitleTextColor: "#000000",
        buttonColor:{
          active:"#000000",
          inactiveBackground:"#cccccc",
          inactive:"#808080"
        }
    };
    }
    return {
      viewBackgroundColor: "#000000",
      progressViewBackgroundColor:"#FFFFFF",
      progressBarColor:"red",
      titleTextColor: "#FFFFFF",
      subtitleTextColor: "#FFFFFF",
      buttonColor:{
        active:"#FFFFFF",
        inactiveBackground:"#cccccc",
        inactive:"#808080"
      }
    }
  };