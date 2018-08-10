(function() {
    'use strict';
    angular
        .module('acmeApp')
        .factory('Questionnaire', Questionnaire);

    Questionnaire.$inject = ['$resource', 'DateUtils'];

    function Questionnaire ($resource, DateUtils) {
        var resourceUrl =  'api/questionnaires/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.created = DateUtils.convertDateTimeFromServer(data.created);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
