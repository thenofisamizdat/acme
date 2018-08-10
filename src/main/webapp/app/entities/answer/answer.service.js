(function() {
    'use strict';
    angular
        .module('acmeApp')
        .factory('Answer', Answer);

    Answer.$inject = ['$resource', 'DateUtils'];

    function Answer ($resource, DateUtils) {
        var resourceUrl =  'api/answers/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.answeredDate = DateUtils.convertDateTimeFromServer(data.answeredDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
