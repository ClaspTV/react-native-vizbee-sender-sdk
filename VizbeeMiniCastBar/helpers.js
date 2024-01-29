/**
 * Gets the miniplayer title to be displayed
 *
 * @returns {string} title to display
 */
export const getTitleFromVideoInfo = ({ title, castingTo }) => {
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
export const getSubTitleFromVideoInfo = ({ title, subTitle, castingTo }) => {
  if (subTitle && subTitle?.length) {
    return subTitle;
  }

  if (title && title?.length) {
    return `Casting to ${castingTo}`;
  }

  return "";
};
